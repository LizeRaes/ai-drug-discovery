package ma.devoxx.langchain4j.aiservices.claude;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Supplier;

@ApplicationScoped
public class AnthropicModelSupplier implements Supplier<ChatLanguageModel> {

    @Override
    public ChatLanguageModel get() {
        return AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName("claude-3-haiku-20240307")
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
