package ma.devoxx.langchain4j.state;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.function.Supplier;

@Singleton
public class CustomChatMemoryProvider implements Supplier<ChatMemoryProvider> {

    @Override
    public ChatMemoryProvider get() {
        return new ChatMemoryProvider() {

            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .maxMessages(20)
                        .id(memoryId)
                        .build();
            }
        };
    }
}