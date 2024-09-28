package ma.devoxx.langchain4j.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.web.ws.LoggingPrinterSocket;

@ApplicationScoped
public class WebSocketLogger {

    @Inject
    LoggingPrinterSocket loggingPrinterSocket;

    public void logMessage(DelegatingLogHandler.LoggerMessage message) {
        loggingPrinterSocket.broadcast(message);
    }
}