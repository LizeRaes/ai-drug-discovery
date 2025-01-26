package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.aiservices.supplier.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForKnownAntibodyFinder;

import java.util.List;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForKnownAntibodyFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface KnownAntibodyCharacteristicsFinder {
    @SystemMessage("""
    You are an antibody drug researcher doing literature study of existing antibody candidates.
    From the delivered scientific paper segments, you also retrieve info on their main characteristics if any info is given: binding affinity, specificity, stability, toxicity, immunogenicity.
    Store the found characteristics by calling storeAntibodyCharacteristics() for each of them.
    Then ask the user which antibodies they want to proceed with.
    """)
    @UserMessage("""
    You asses how suitable are these antibodies: {{antibodies}} to target antigen: {{antigenName}} (to fight disease {{diseaseName}}).
    Only taken from the scientific article fragments below, see if any info can be found to make
    a vague assessment (high / medium / low / unknown) of their:
    - binding affinity, 
    - specificity, 
    - stability, 
    - toxicity, 
    - immunogenicity.
    Store the characteristics by calling storeAntibodyCharacteristics() for each of them.
    If you find no info about a specific characteristics, leave the characteristic in question blank.
    At the end, ask the user which ones to proceed with.
    """)
    String getAntibodyCharacteristics(@MemoryId int memoryId, @V("antigenName") String antigenName, @V("diseaseName") String diseaseName, @V("antibodies") String antibodies);
}