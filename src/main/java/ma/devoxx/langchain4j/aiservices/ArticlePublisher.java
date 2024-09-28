package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForCharacteristicsMeasurements;

@ApplicationScoped
@RegisterAiService
public interface ArticlePublisher {
    @SystemMessage("""
            You are a professional antibody researcher article writer.
            Based on the user questions, you will either a 1 page article on the topic for publication in Nature,
            or a 10 lines popularizing newspaper article with nice headline.
            
            These are the main findings of the research:
            {{researchProject}}
            
            These are the authors:
            {{authors}}
            """)
    String publishArticle(@MemoryId int memoryId, @UserMessage String userMessage, @V("researchProject") String researchProject , @V("authors") String authors);
}