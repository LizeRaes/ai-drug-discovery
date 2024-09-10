package ma.devoxx.langchain4j.text;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.aiservices.FullResearcherService;
import ma.devoxx.langchain4j.printer.MyService;
import ma.devoxx.langchain4j.printer.MyWebSocket;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.state.CustomChatMemory;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;
import ma.devoxx.langchain4j.tools.ToolsForFullResearch;
import org.jboss.logging.Logger;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@Path("/statemessage")
public class StateTextResource {

    Logger logger = Logger.getLogger(TextResource.class);

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    @Inject
    MyWebSocket myWebSocket;

    @Inject
    MyService myService;

    @Inject
    CustomChatMemory customChatMemory;

    @Inject
    CustomResearchProject cumstomResearchProject;

    @Inject
    ToolsForDiseasePicker toolsForDiseasePicker;

    @Inject
    CustomRetrievalAugmentor customRetrievalAugmentor;

    StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
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

        // TODO watch out with the memory, if we need another memory in later steps
        DiseasePicker diseasePicker = AiServices.builder(DiseasePicker.class)
                .streamingChatLanguageModel(model)
                .chatMemory(customChatMemory.getChatMemory())
                //.retrievalAugmentor(retrievalAugmentor)
                //.retrievalAugmentor(getRetrievalAugmentor()) to use other documents
                .tools(new ToolsForDiseasePicker())
                .build();

        if(ResearchStateMachine.getCurrentStep(cumstomResearchProject.getResearchProject()).startsWith("1")) {
                logger.info("IN STEP 1 (define target disease)");
                diseasePicker.answer(message)
                .onNext(token -> {
                    System.out.println(token);
                    myService.sendMessage(session, token);
                })
                .onComplete(response -> logger.info("\n\nDone streaming"))
                .onError(error -> logger.info("Something went wrong: " + error.getMessage()))
                .start();

                // model will set diseaseName and currentStep = 2 when decided on disease

                return Response.ok().build();
        }

        if(ResearchStateMachine.getCurrentStep(cumstomResearchProject.getResearchProject()).startsWith("2")) {
                logger.info("STARTING STEP 2 (find antigen)");
                AntigenFinder antigenFinder = AiServices.builder(AntigenFinder.class)
                .streamingChatLanguageModel(model)
                //.chatMemory(customChatMemory.getChatMemory())
                .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                //.retrievalAugmentor(getRetrievalAugmentor()) to use other documents
                .tools(new ToolsForAntigenFinder())
                .build();

                // model will set antigenInfo and currentStep = 3 when decided on antigen
                // continue with next step
        }
        
        // if something went wrong nonetheless
        if(ResearchStateMachine.getCurrentStep(cumstomResearchProject.getResearchProject()).startsWith("3")) {
                logger.info("UNEXPECTED STEP: " +ResearchStateMachine.getCurrentStep(cumstomResearchProject.getResearchProject()));
                return Response.ok().build();
        }

        // execute step 3
        logger.info("STARTING STEP 3 (find antibodies)");
        // TODO continue to build flow

        return Response.ok().build();
        
    }

}
