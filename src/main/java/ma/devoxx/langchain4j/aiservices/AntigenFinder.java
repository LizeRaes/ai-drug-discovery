package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.aiservices.supplier.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForAntigenFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface AntigenFinder {
    @SystemMessage("""
    You search through sources for a suitable antigen (characteristic protein) for the given disease and then determine it's sequence (the sequence for the antigen. a disease has no sequence and is no protein.)
    Then you call storeAntigenInfo with the antigen name and sequence (only light chain and heavy chain, omit the empty CDRs.
    """)
    @UserMessage("Find a suitable antigen for {{diseaseName}}")
    String determineAntigenInfo(@V("diseaseName") String diseaseName);
}
