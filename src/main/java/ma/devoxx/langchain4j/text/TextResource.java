package ma.devoxx.langchain4j.text;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.devoxx.langchain4j.aiservices.FullResearcherService;
import ma.devoxx.langchain4j.aiservices.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.printer.MyService;
import ma.devoxx.langchain4j.printer.MyWebSocket;
import ma.devoxx.langchain4j.tools.ToolsForFullResearch;
import org.jboss.logging.Logger;

@Path("/message")
public class TextResource {

    Logger logger = Logger.getLogger(TextResource.class);

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    @Inject
    MyWebSocket myWebSocket;

    @Inject
    MyService myService;

    @Inject
    ToolsForFullResearch toolsForFullResearch;

    @Inject
    CustomRetrievalAugmentor customRetrievalAugmentor;

    StreamingChatLanguageModel streamingModel = OpenAiStreamingChatModel.builder()
            .apiKey(apiKey)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .logRequests(true)
            .logResponses(true)
            .build();

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(String message) {
        logger.info(message);
        Session session = myWebSocket.getSessionById();

        FullResearcherService assistant = AiServices.builder(FullResearcherService.class)
                .streamingChatLanguageModel(streamingModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                //.retrievalAugmentor(getRetrievalAugmentor()) to use other documents
                .tools(toolsForFullResearch)
                .build();

        assistant.answer(message)
                .onNext(token -> {
                    //System.out.print(token);
                    myService.sendMessage(session, token);
                })
                .onComplete(response -> logger.info("\n\nDone streaming"))
                .onError(error -> logger.info("Something went wrong: " + error.getMessage()))
                .start();

        return Response.ok().build();
    }
}
