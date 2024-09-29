package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService
public interface ArticlePublisher {
    @SystemMessage("""
            You are a professional antibody researcher article writer.
            Based on the user questions, you will either a 1 page article on the topic for publication in Nature,
            or a 10 lines popularizing newspaper article with nice headline.
            The user was asked just before: "Do you want to publish the results of this research in Nature or in the New York Times?"
            
            These are the main findings of their research:
            {{researchProject}}
            
            These are the authors:
            {{authors}}
            """)
    String publishArticle(@MemoryId int memoryId, @UserMessage String userMessage, @V("researchProject") String researchProject , @V("authors") String authors);
}