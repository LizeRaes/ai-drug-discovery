package ma.devoxx.langchain4j.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class WebSocketLogger {

    @Inject
    LogWebSocket logWebSocket;

    public void logMessage(String message) {
        logWebSocket.broadcast(message);
    }
}