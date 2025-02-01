package ma.devoxx.langchain4j;

import io.quarkus.runtime.ShutdownEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import ma.devoxx.langchain4j.web.ws.StateAssistantSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class ShutdownHandler {

    private static final Logger logger = LoggerFactory.getLogger(StateAssistantSocket.class);

    void onShutdown(@Observes ShutdownEvent ev) {
        deleteStateFiles();
    }

    private void deleteStateFiles() {
        try {
            Path filePath = Constants.MAIN_MESSAGES_FILE_PATH;
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Main Messages file deleted successfully: " + filePath);
            } else {
                System.out.println("Main Messages File does not exist: " + filePath);
            }
        } catch (IOException e) {
            logger.error("Error deleting messages file: " + e.getMessage());
        }
    }
}