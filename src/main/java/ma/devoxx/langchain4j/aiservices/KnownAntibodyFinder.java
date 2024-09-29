package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.aiservices.supplier.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForKnownAntibodyFinder;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForKnownAntibodyFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface KnownAntibodyFinder {
    @SystemMessage("""
    You are an antibody drug researcher doing literature study of existing antibody candidates 
    to target given antigen. You propose suitable known antibodies for the antigen from the delivered scientific paper segments. 
    You also retrieve info on their main characteristics if any info is given: binding affinity, specificity, stability, toxicity, immunogenicity.
    Store the found antibodies and their characteristics by calling storeAntibody() for each of them.
    Then ask the user which antibodies they want to proceed with.
    """)
    @UserMessage("""
    Only taken from the scientific article fragments below, list suitable monoclonal antibodies (add mAb to the search term or it will not work!) that target antigen: {{antigenName}} (to fight disease {{diseaseName}}).
    Make an assessment of their binding affinity, specificity, stability, toxicity, and immunogenicity
    Store the found antibodies and their characteristics by calling storeAntibody() for each of them.
    If you find no info about a specific characteristics, leave the characteristic in question blank.
    At the end, ask the user which ones to proceed with.
    """)
    String getAntibodies(@MemoryId int memoryId, @V("antigenName") String antigenName, @V("diseaseName") String diseaseName);
}