package ma.devoxx.langchain4j.domain;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@ApplicationScoped
public class StateMemoryProvider implements Supplier<ChatMemoryProvider> {

    @Override
    public ChatMemoryProvider get() {
        PersistentChatMemoryStore store = new PersistentChatMemoryStore(new StateManager());
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();
    }

    static class PersistentChatMemoryStore implements ChatMemoryStore {

        StateManager stateManager;

        private final Map<Integer, String> map = new LinkedHashMap<>();

        public PersistentChatMemoryStore(StateManager stateManager) {
            this.stateManager = stateManager;
        }

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            List<ChatMessage> messages = stateManager.loadChatMessage();
            String json = messagesToJson(messages);
            map.put(1, json);
            return messages;
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            map.put(1, json);
            save();
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove(1);
            save();
        }

        public void save() {
            try {
                if (map.containsKey(1)) {
                    Files.writeString(Constants.MAIN_MESSAGES_FILE_PATH, map.get(1));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
