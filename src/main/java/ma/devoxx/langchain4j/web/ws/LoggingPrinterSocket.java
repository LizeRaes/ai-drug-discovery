package ma.devoxx.langchain4j.web.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.util.StringUtil;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import kotlin.text.StringsKt;
import ma.devoxx.langchain4j.logging.LoggerMessage;
import org.jboss.logmanager.ExtLogRecord;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/logs")
@ApplicationScoped
public class LoggingPrinterSocket {

    private static final Logger LOGGER = Logger.getLogger(LoggingPrinterSocket.class.getName());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    public void broadcast(ExtLogRecord record) {
        if (!StringUtil.isNullOrEmpty(record.getMessage())) {
            sessions.forEach(session -> {
                if (session.isOpen()) {
                    Uni.createFrom().item(() -> formatMessage(record))
                            .onItem()
                            .transformToUni(msg -> {
                                CompletableFuture<Void> future = new CompletableFuture<>();
                                session.getAsyncRemote().sendText(msg, result -> {
                                    if (result.isOK()) {
                                        future.complete(null);
                                    } else {
                                        future.completeExceptionally(result.getException());
                                    }
                                });
                                return Uni.createFrom().future(future);
                            })
                            .subscribe()
                            .with(
                                    success -> LOGGER.log(Level.FINEST, "Message sent successfully"),
                                    failure -> LOGGER.log(Level.FINEST, "Failed to send message")
                            );

                }
            });
        }
    }

    public String formatMessage(ExtLogRecord record) {
        try {
            String formattedTimestamp = formatTimestamp(record.getInstant());
            String logMessage = buildLogMessage(record, formattedTimestamp);
            LoggerMessage loggerMessage = new LoggerMessage(
                    determineLogColor(record.getLoggerName(), record.getMessage()), logMessage);

            return mapper.writeValueAsString(loggerMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatTimestamp(Instant timestamp) {
        return DATE_FORMATTER.format(timestamp);
    }

    // Builds the log message with timestamp, level, message, and optional stack trace
    public String buildLogMessage(ExtLogRecord record, String formattedTimestamp) {
        String basicMessage;
        if (record.getParameters() != null && record.getParameters().length > 0) {
            String formattedMessage = String.format(record.getMessage(), record.getParameters());

            // Prefix assignment
            String prefix = "DEFAULT: ";
            if (formattedMessage.contains("POST")) {
                prefix = "USER: ";
                formattedMessage = extractLastUserQuestion(formattedMessage);
            } else if (formattedMessage.contains("status code")) {
                prefix = "AI MODEL: ";
                formattedMessage = extractLastAssistantResponse(formattedMessage);
            }

            basicMessage = String.format("[%s] [%s] " + prefix + formattedMessage, formattedTimestamp, record.getLevel());
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

    public String extractLastUserQuestion(String response) {
        try {
            // Split based on "role": "user"
            String[] parts = response.split("\"user\"");
            if (parts.length > 1) {
                String lastUserPart = parts[parts.length - 1];
                int contentStart = lastUserPart.indexOf("\"content\"") + 10;
                if (contentStart == 9) return ""; // Fail-safe

                int contentEnd = lastUserPart.indexOf("}", contentStart);
                if (contentEnd == -1) return lastUserPart.substring(contentStart).trim(); // If no proper closing

                return lastUserPart.substring(contentStart, contentEnd).trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Extracts the last assistant response by splitting on "role": "assistant"
    public String extractLastAssistantResponse(String response) {
        try {
            // Split the entire response based on "role": "assistant"
            String[] parts = response.split("\"assistant\"");

            if (parts.length > 1) {
                // Get the last part (last assistant response)
                String lastAssistantPart = parts[parts.length - 1];

                // Find the start of "content":
                int contentStart = lastAssistantPart.indexOf("\"content\"") + 10; // Move past "content":
                if (contentStart == 9) return response; // Fail-safe (indexOf returns -1)

                // Extract substring from "content": until the next '}'
                int contentEnd = lastAssistantPart.indexOf("}", contentStart);
                if (contentEnd == -1) return response; // Fail-safe

                return lastAssistantPart.substring(contentStart, contentEnd).trim();
            } else {
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response; // Return original if extraction fails
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
}