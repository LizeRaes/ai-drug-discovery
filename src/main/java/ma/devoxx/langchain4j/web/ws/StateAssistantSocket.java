package ma.devoxx.langchain4j.web.ws;

import io.quarkus.websockets.next.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import ma.devoxx.langchain4j.Constants;
import ma.devoxx.langchain4j.domain.Message;
import ma.devoxx.langchain4j.domain.StateMachine;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
@WebSocket(path = "/my-websocket-state")
public class StateAssistantSocket {

    private static final Logger logger = LoggerFactory.getLogger(StateAssistantSocket.class);

    WebSocketConnection connection;

    private Integer userId;

    @Inject
    StateMachine stateMachine;

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    CustomResearchState customResearchState;

    @Inject
    Jsonb jsonb;

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        try {
            this.connection = connection;
            refreshUser();
            System.out.println("Session opened, ID: " + connection.id());
            init();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

    }

    public void init() {
        if (connection.isOpen()) {
            sendJsonMessage(connection, Message.aiMessage("Hi, I’m here to assist you with your antibody research today."));
            stateMachine.init(m -> sendJsonMessage(connection, m));
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
        System.out.println("Session closed, ID: " + connection.id());
        final String sessionId = connection.id();

        customResearchProject.clear();
        customResearchState.clear();

        logger.info("Session closed, ID: {}", sessionId);
    }

    public void refreshUser() {
        userId = (int) (Math.random() * 1000) + 1;
    }
}

