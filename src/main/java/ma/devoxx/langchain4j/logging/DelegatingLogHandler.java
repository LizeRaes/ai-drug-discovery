package ma.devoxx.langchain4j.logging;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class DelegatingLogHandler extends ExtHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final WebSocketLogger webSocketLogger;

    public DelegatingLogHandler(WebSocketLogger webSocketLogger) {
        this.webSocketLogger = webSocketLogger;
    }

    @Override
    protected void doPublish(ExtLogRecord record) {
        String formattedTimestamp = formatTimestamp(record.getInstant());
        String logMessage = buildLogMessage(record, formattedTimestamp);
        LoggerMessage loggerMessage = new LoggerMessage(
                determineLogColor(record.getLoggerName(), record.getMessage()), logMessage);

        webSocketLogger.logMessage(loggerMessage);
    }

    private String formatTimestamp(Instant timestamp) {
        return DATE_FORMATTER.format(timestamp);
    }

    // Builds the log message with timestamp, level, message, and optional stack trace
    public String buildLogMessage(ExtLogRecord record, String formattedTimestamp) {
        String basicMessage;
        if (record.getParameters() != null && record.getParameters().length > 0) {
            Object[] allParams = combineArrays(new Object[]{
                    formattedTimestamp, record.getLevel()}, record.getParameters());
            basicMessage = String.format("[%s] [%s] " + record.getMessage(), allParams);
        } else {
            basicMessage = String.format("[%s] [%s] %s",
                    formattedTimestamp,
                    record.getLevel(),
                    record.getMessage());
        }

        // Append stack trace if an exception is present
        if (record.getThrown() != null) {
            basicMessage += "\n" + getStackTraceAsString(record.getThrown());
        }
        return basicMessage;
    }

    private static Object[] combineArrays(Object[] first, Object[] second) {
        Object[] result = new Object[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
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
    private String determineLogColor(String loggerName, String loggerMessage) {
        if (loggerName.contains("Tools")) {
            return "yellow";
        } else if (loggerName.contains("Ingestor")
                || loggerName.contains("Retriev")
                || loggerName.contains("ContentAggregator")) {
            return "red";
        } else if (loggerMessage.startsWith("****")) {
            return "purple";
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
