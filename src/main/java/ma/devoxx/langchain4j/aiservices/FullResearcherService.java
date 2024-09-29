package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.guardrails.InputGuardrails;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.guardrails.NoBiologicalWeapon;
import ma.devoxx.langchain4j.aiservices.supplier.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.*;

@ApplicationScoped
@RegisterAiService(
        tools = {ToolsForDiseasePicker.class,
                ToolsForAntigenFinder.class,
                ToolsForKnownAntibodyFinder.class,
                ToolsForCdrFinder.class,
                ToolsForNewAntibodyFinder.class,
                ToolsForCharacteristicsMeasurements.class},
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface FullResearcherService {
    @InputGuardrails(NoBiologicalWeapon.class)
    @SystemMessage("""
            You are an AI researcher assistant that will help find antibody drug solutions.
            You assist the user in walking through these steps, always asking for confirmation before starting the next step.
            1. Define target disease for antibody research (user input required), set storeDiseaseName and then show printProjectState to the user
            important: ignore all extra resources and fragments when the user wants to determine a disease to work on, just have a conversation until the user made up his mind 
            2. Find antigen name and then sequence for target disease, set storeAntigenInfo and then printProjectState to the user
            3. Find known antibodies for target disease and their characteristics (binding affinity, specificity, stability, toxicity, immunogenicity)
            4. Find CDRs for the known antibodies,
            5. Find new candidate antibody based on antigen sequence and known antibodies,
            6. Determine characteristics of new candidate antibody (binding affinity, specificity, stability, toxicity, immunogenicity) (user permission required to proceed with calling those tool)
            7. Ask user if they want an article out of the findings for publishing in Nature or for the New York Times (keep the article under one page)
            """)
    String answer(@MemoryId int memoryId, @UserMessage String query);
}


