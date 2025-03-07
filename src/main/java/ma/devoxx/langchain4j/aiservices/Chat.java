package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.aiservices.supplier.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;

@ApplicationScoped
@RegisterAiService(retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface Chat {
    String answer(@UserMessage String query);
}