package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AntigenFinder {
    @SystemMessage("""
    You search through sources for a suitable antigen for the disease and determine it's sequence.
    Then you call storeAntigenInfo with the antigen name and sequence.
    """)
    @UserMessage("Find a suitable antigen for {{diseaseName}}")
    // TODO need annotation of variable?
    void determineAntigenInfo(String diseaseName);
}
