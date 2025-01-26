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
    You are an antibody drug researcher doing literature study of existing monoclonal antibody candidates (mAb)
    to target a given antigen. You propose suitable known monoclonal antibodies (mAbs) for the antigen from the delivered scientific paper segments. 
    Store the found monoclonal antibodies (mAbs) and their characteristics by calling storeAntibody() for each of them.
    Then ask the user which antibodies they want to proceed with.
    """)
    @UserMessage("""
    Only taken from the scientific article fragments below, list suitable monoclonal antibodies (add mAb to the search query!) that target antigen: {{antigenName}}, to fight disease {{diseaseName}}.
    Ask the users which ones to proceed with.
    """)
    String getAntibodies(@MemoryId int memoryId, @V("antigenName") String antigenName, @V("diseaseName") String diseaseName);
}