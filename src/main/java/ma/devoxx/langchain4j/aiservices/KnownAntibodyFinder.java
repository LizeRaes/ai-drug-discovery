package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForKnownAntibodyFinder;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForKnownAntibodyFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface KnownAntibodyFinder {
    @SystemMessage("""
    You are an antibody drug researcher doing literature study of existing antibody candidates 
    to target antigen {{antigenName}}. You propose suitable known antibodies for the antigen, 
    as well as their main characteristics (binding affinity, specificity, stability, toxicity, immunogenicity)
    if you find info about them. 
    Store the proposed antibodies and their characteristics by calling storeAntibody() for each of them.
    Then ask the user which antibodies they want to proceed with.
    """)
    @UserMessage("""
    From the information below, give me suitable antibodies and an assessment of their binding affinity, 
    specificity, stability, toxicity, and immunogenicity if you find info about them. Also ask me which ones to proceed with.
    """)
    String getAntibodies(@MemoryId int memoryId, @V("antigenName") String antigenName);
}