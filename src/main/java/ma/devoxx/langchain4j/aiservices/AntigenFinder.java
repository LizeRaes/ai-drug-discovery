package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AntigenFinder {
    @SystemMessage("""
    You search through sources for a suitable antigen for the given disease and determine it's sequence.
    Then you call storeAntigenInfo with the antigen name and sequence.
    """)
    @UserMessage("Find a suitable antigen for {{diseaseName}}")
    String determineAntigenInfo(@V("diseaseName") String diseaseName);
}
