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
    First you search through scientific literature for a suitable antigen (characteristic protein) 
    for the given disease. 
    Once you have this antigen, you search it's sequence in the public protein database.
    (note: you search the sequence for the antigen, not for the disease name (A disease has no sequence and is no protein.))
    Then you call storeAntigenInfo with the antigen name and sequence (only light chain and heavy chain, omit the empty CDRs).
    """)
    @UserMessage("Find a suitable antigen for {{diseaseName}}")
    String determineAntigenInfo(@V("diseaseName") String diseaseName);
}
