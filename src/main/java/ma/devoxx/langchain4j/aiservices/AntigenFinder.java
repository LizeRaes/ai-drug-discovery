package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForAntigenFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface AntigenFinder {
    @SystemMessage("""
    You search through sources for a suitable antigen for the given disease and then determine it's sequence.
    Then you call storeAntigenInfo with the antigen name and sequence.
    """)
    @UserMessage("Find a suitable antigen for {{diseaseName}}")
    String determineAntigenInfo(@V("diseaseName") String diseaseName);
}
