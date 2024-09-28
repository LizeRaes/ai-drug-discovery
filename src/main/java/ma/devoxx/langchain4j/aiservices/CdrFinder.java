package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForCdrFinder;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForCdrFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface CdrFinder {
    @SystemMessage("""
            Help the user decide on which antibodies to proceed with if they are unsure.
            Once they clearly manifest or choose which ones to proceed with,
            store the chosen antibodies using storeCdr() for each of them, 
            tell which CDRs you found, and do not ask further questions.
            """)
    @UserMessage("""
            Return and store suitable CDRs (CDR_L1, CDR_L2, ... till CDR_H3) for the selected antibodies: 
            {{usrMsg}}.
            """)
    String getCdrs(@MemoryId int memoryId, @V("usrMsg") String userMsg);
}