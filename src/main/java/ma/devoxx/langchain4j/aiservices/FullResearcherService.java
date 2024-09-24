package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import io.quarkiverse.langchain4j.RegisterAiService;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.tools.ToolsForFullResearch;

@RegisterAiService(
        tools = ToolsForFullResearch.class)
public interface FullResearcherService {
    @SystemMessage("""
        You are an AI researcher assistant that will help find antibody drug solutions.
        You assist the user in walking through these steps, always asking for confirmation before starting the next step.
        1. Define target disease for antibody research (user input required), set storeDiseaseName and then show printProjectState to the user
        important: ignore all extra resources and fragments when the user wants to determine a disease to work on, just have a conversation until the user made up his mind 
        2. Find antigen name and then sequence for target disease, set storeAntigenInfo and then printProjectState to the user
        3. Find known antibodies for target disease and their characteristics (binding affinity, specificity, stability, toxicity, immunogenicity) then printProjectState
        4. Find CDRs for the known antibodies, then printProjectState
        5. Find new candidate antibody based on antigen sequence and known antibodies, then printProjectState
        6. Determine characteristics of new candidate antibody (binding affinity, specificity, stability, toxicity, immunogenicity) (user permission required to proceed with calling those tool), then printProjectState
            """)
    TokenStream answer(String query);
}


