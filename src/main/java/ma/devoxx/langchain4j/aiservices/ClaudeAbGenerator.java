package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(modelName = "test")
public interface ClaudeAbGenerator {
    @UserMessage("""
            Propose CDRs for a new antibody for antigen {{antigenSequence}}
            You can base yourself on insights gained from these known antibodies:
            {{previousAntibodies}}
            The CDRs should be in the format CDR-L1, CDR-L2, CDR-L3, CDR-H1, CDR-H2, CDR-H3.
            Please give 3 lines of explanation for why you chose these CDRs.
            """)
    String generateNewAbSuggestion(@V("antigenSequence") String antigenSequence, @V("previousAntibodies") String previousAntibodies);
}
