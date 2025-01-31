package ma.devoxx.langchain4j.logging;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;


public class DelegatingLogHandler extends ExtHandler {

    private final WebSocketLogger webSocketLogger;

    public DelegatingLogHandler(WebSocketLogger webSocketLogger) {
        this.webSocketLogger = webSocketLogger;
    }

    @Override
    protected void doPublish(ExtLogRecord record) {
        webSocketLogger.logMessage(record);
    }
}
