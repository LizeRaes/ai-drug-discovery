package ma.devoxx.langchain4j.domain;

import dev.langchain4j.data.message.ChatMessage;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class StateManagerTest {

    @Inject
    StateManager stateManager;

    @Test
    void test() {
        List<ChatMessage> messages = stateManager.loadChatMessage();

        assertEquals(7, messages.size());
    }
}