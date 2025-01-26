package ma.devoxx.langchain4j.domain;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageJsonCodec;
import dev.langchain4j.spi.data.message.ChatMessageJsonCodecFactory;
import io.quarkiverse.langchain4j.QuarkusChatMessageJsonCodecFactory;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import ma.devoxx.langchain4j.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
@ApplicationScoped
public class StateManager {

    public List<ChatMessage> loadChatMessage(String name) {
        try {
            String conversationJson = Files.readString(Constants.MAIN_MESSAGES_FILE_PATH);
            ChatMessageJsonCodecFactory chatMessageJsonCodecFactory = new QuarkusChatMessageJsonCodecFactory();
            ChatMessageJsonCodec codec = chatMessageJsonCodecFactory.create();
            return codec.messagesFromJson(conversationJson);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatMessage> loadChatMessage() {
        return loadChatMessage(null);
    }
}
