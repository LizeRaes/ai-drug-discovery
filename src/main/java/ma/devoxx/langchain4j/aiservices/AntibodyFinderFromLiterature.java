package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

import java.util.List;

public interface AntibodyFinderFromLiterature {
    @SystemMessage("""
    Given the antigen name, you propose names of good candidate antibodies.
    """)
    @UserMessage("""
    Which antibodies would be good candidates for {{antigenName}}?
    """)
    List<String> getAntibodies(String antigenName);
}