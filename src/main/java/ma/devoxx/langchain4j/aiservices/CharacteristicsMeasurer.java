package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.tools.ToolsForCharacteristicsMeasurements;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForCharacteristicsMeasurements.class)
public interface CharacteristicsMeasurer {
    @UserMessage("""
    Given the newly designed antibody:
    {{antibodyName}}
    with CDRs:
    {{CDRs}}
    
    Proposed to target the antigen:
    {{antigen}}
    
    Measure the binding affinity, specificity, stability, toxicity, and immunogenicity of the antibody, 
    and store the measurement values.
    """)
    String measureCharacteristics(@V("antibodyName") String antibodyName,@V("CDRs") String CDRs, @V("antigen") String antigen);
}