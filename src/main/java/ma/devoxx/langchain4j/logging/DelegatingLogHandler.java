package ma.devoxx.langchain4j.logging;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class DelegatingLogHandler extends ExtHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final WebSocketLogger webSocketLogger;

    public DelegatingLogHandler(WebSocketLogger webSocketLogger) {
        this.webSocketLogger = webSocketLogger;
    }

    @Override
    protected void doPublish(ExtLogRecord record) {
        // Get the timestamp of the log record
        Instant timestamp = record.getInstant();
        String formattedTimestamp = FORMATTER.format(timestamp);

        // Format the log message with logger name, level, and message
        String logMessage = String.format("[%s] [%s] %s - %s",
                formattedTimestamp, // Add formatted timestamp
                record.getLevel(),
                record.getLoggerName(),
                record.getMessage()
        );

        // Check if the log record contains an exception
        if (record.getThrown() != null) {
            // Capture the stack trace as a string
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            String stackTrace = sw.toString();

            // Append the stack trace to the log message
            logMessage += "\n" + stackTrace;
        }

        webSocketLogger.logMessage(logMessage);
    }

    @Override
    public void flush() {
        // Optional: implement if necessary
    }

    @Override
    public void close() throws SecurityException {
        // Optional: implement if necessary
    }

    static class ExtLogFormatter extends Formatter {

        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            // Cast to ExtLogRecord if possible
            ExtLogRecord extRecord = (record instanceof ExtLogRecord) ? (ExtLogRecord) record : null;

            StringBuilder sb = new StringBuilder();

            // Date and Time
            sb.append(dateFormat.format(new Date(record.getMillis()))).append(" ");

            // Log Level
            sb.append("[").append(record.getLevel()).append("] ");

            // Class Name (from ExtLogRecord if available)
            if (extRecord != null && extRecord.getLoggerClassName() != null) {
                sb.append(extRecord.getLoggerClassName()).append(".");
            }

            // Method Name (from ExtLogRecord if available)
            if (extRecord != null && extRecord.getLoggerName() != null) {
                sb.append(extRecord.getLoggerName()).append(" - ");
            }

            // Log Message
            sb.append(record.getMessage());

            return sb.toString();
        }
    }
}