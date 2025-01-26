package ma.devoxx.langchain4j.web.ws;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import ma.devoxx.langchain4j.domain.Message;
import ma.devoxx.langchain4j.domain.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-state")
public class StateAssistantSocket {

    private static final Logger logger = LoggerFactory.getLogger(StateAssistantSocket.class);

    private Integer userId;

    @Inject
    StateMachine stateMachine;

    @Inject
    Jsonb jsonb;

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        try {
            System.out.println("Session opened, ID: " + connection.id());
            sendJsonMessage(connection, Message.aiMessage("Hi, I’m here to assist you with your antibody research today."));
            stateMachine.init(m -> sendJsonMessage(connection, m));
            refreshUser();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, String userMessage) {
        System.out.println("Received message: " + userMessage);

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        stateMachine.run(userId, userMessage, m -> sendJsonMessage(connection, m));
    }

    private void sendJsonMessage(WebSocketConnection connection, Message message) {
        connection.sendTextAndAwait(jsonb.toJson(message));
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

