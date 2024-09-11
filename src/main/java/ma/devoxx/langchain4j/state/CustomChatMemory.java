package ma.devoxx.langchain4j.state;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomChatMemory {

    ChatMemory chatMemory;

    public CustomChatMemory() {
        chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    }

    public ChatMemory getChatMemory() {
        return chatMemory;
    }

}
