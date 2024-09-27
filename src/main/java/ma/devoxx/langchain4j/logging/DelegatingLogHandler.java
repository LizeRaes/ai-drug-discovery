package ma.devoxx.langchain4j.logging;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class DelegatingLogHandler extends ExtHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final WebSocketLogger webSocketLogger;

    public DelegatingLogHandler(WebSocketLogger webSocketLogger) {
        this.webSocketLogger = webSocketLogger;
    }

    @Override
    protected void doPublish(ExtLogRecord record) {
        String formattedTimestamp = formatTimestamp(record.getInstant());
        String logMessage = buildLogMessage(record, formattedTimestamp);
        LoggerMessage loggerMessage = new LoggerMessage(determineLogColor(record.getLoggerName()), logMessage);

        webSocketLogger.logMessage(loggerMessage);
    }

    private String formatTimestamp(Instant timestamp) {
        return DATE_FORMATTER.format(timestamp);
    }

    // Builds the log message with timestamp, level, message, and optional stack trace
    private String buildLogMessage(ExtLogRecord record, String formattedTimestamp) {
        String basicMessage = String.format("[%s] [%s] %s",
                formattedTimestamp,
                record.getLevel(),
                record.getMessage());

        // Append stack trace if an exception is present
        if (record.getThrown() != null) {
            basicMessage += "\n" + getStackTraceAsString(record.getThrown());
        }
        return basicMessage;
    }

    // Converts a stack trace to a string
    private String getStackTraceAsString(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
        }
        return sw.toString();
    }

    // Determines the log color based on the logger name
    private String determineLogColor(String loggerName) {
        if (loggerName.contains("Tools")) {
            return "yellow";
        } else if (loggerName.contains("Ingestor") || loggerName.contains("Retriev")) {
            return "red";
        }
        return "white";
    }

    public static class LoggerMessage {
        private final String color;
        private final String message;

        public LoggerMessage(String color, String message) {
            this.color = color;
            this.message = message;
        }

        public String getColor() {
            return color;
        }

        public String getMessage() {
            return message;
        }
    }
}
