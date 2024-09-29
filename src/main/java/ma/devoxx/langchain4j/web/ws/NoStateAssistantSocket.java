package ma.devoxx.langchain4j.web.ws;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.FullResearcherService;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-no-state")
public class NoStateAssistantSocket {

    private static final Logger logger = LoggerFactory.getLogger(NoStateAssistantSocket.class);

    private Integer userId;

    @Inject
    CustomResearchState customResearchState;

    @Inject
    FullResearcherService fullResearcherService;

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        System.out.println("Session opened, ID: " + connection.id());
        customResearchState.getResearchState().moveToStep(ResearchState.Step.DEFINE_DISEASE);
        refreshUser();
        connection.sendTextAndAwait("Hi, I’m here to assist you with your antibody research today.");
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

