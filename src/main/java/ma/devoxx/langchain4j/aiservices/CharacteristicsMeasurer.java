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
    {{antigenSequence}}
    
    Measure the binding affinity, specificity, stability, toxicity, and immunogenicity of the antibody, 
    and store the measurement values.
    
    Also return to the user the url to '.pbd' structure file of the antigen {{antigenName}}, and mention this was found with AlphaFold.
    
    At the end as a reminder, tell the user the new antibodies target the antigen {{antigenName}}.
    """)
    String measureCharacteristics(@V("antibodyName") String antibodyName,@V("CDRs") String CDRs, @V("antigenSequence") String antigenSequence, @V("antigenName") String antigenName);
}