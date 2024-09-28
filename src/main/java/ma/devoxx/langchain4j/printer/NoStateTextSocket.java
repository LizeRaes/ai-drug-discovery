package ma.devoxx.langchain4j.printer;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.FullResearcherService;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.state.CustomChatMemory;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-no-state")
public class NoStateTextSocket {

    private static final Logger logger = LoggerFactory.getLogger(NoStateTextSocket.class);

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    private Integer userId;

    @Inject
    CustomChatMemory customChatMemory;

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    CustomResearchState customResearchState;

    @Inject
    CustomRetrievalAugmentor customRetrievalAugmentor;

    @Inject
    FullResearcherService fullResearcherService;

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        System.out.println("Session opened, ID: " + connection.id());
        customResearchState.getResearchState().moveToStep(ResearchState.Step.DEFINE_DISEASE);
        refreshUser();
    }

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, String userMessage) throws Exception {
        System.out.println("Received message: " + userMessage);
        // retrieve user ID
        final String sessionId = connection.id();

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        String answer = fullResearcherService.answer(1, userMessage);
        logger.info("*** Model Answer ***: " + answer);
        connection.sendTextAndAwait(answer);
    }

    @OnClose
    void onClose(WebSocketConnection connection) {
        final String sessionId = connection.id();

        // release some resources

        logger.info("Session closed, ID: {}", sessionId);
    }

    public void refreshUser() {
        userId = (int) (Math.random() * 1000) + 1;
    }
}

