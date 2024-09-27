package ma.devoxx.langchain4j.printer;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.aiservices.FullResearcherService;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.state.CustomChatMemory;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-no-state")
public class NoStateTextSocket {

    private static final Logger logger = LoggerFactory.getLogger(NoStateTextSocket.class);


    private final String apiKey = System.getenv("OPENAI_API_KEY");

    @Inject
    CustomChatMemory customChatMemory;

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    CustomRetrievalAugmentor customRetrievalAugmentor;

    ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .logRequests(true)
            .logResponses(true)
            .build();

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        System.out.println("Session opened, ID: " + connection.id());

        // TODO init Ai Service here
    }

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, String userMessage) throws Exception {
        System.out.println("Received message: " + userMessage);
        // retrieve user ID
        final String sessionId = connection.id();

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        FullResearcherService fullResearcherService = AiServices.builder(FullResearcherService.class)
                .chatLanguageModel(model)
                .chatMemory(customChatMemory.getChatMemory())
                .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                .tools(new ToolsForAntigenFinder(customResearchProject.getResearchProject()))
                .build();

        String answer = fullResearcherService.answer(userMessage);
        logger.info("*** Model Answer ***: " + answer);
        connection.sendTextAndAwait(answer);
    }

    @OnClose
    void onClose(WebSocketConnection connection) {
        final String sessionId = connection.id();

        // release some resources

        logger.info("Session closed, ID: {}", sessionId);
    }
}

