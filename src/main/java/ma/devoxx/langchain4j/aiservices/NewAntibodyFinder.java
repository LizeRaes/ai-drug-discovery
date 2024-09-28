package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForNewAntibodyFinder;

@ApplicationScoped
@RegisterAiService(
        tools = ToolsForNewAntibodyFinder.class)
public interface NewAntibodyFinder {
    @UserMessage("""
    Propose new CDRs against the following antigen using different functions, and store each of them using storeNewCdrs(). Antigen:
    {{antigen}}.
    You can optionally use the information about the following known antibodies that target this antigen:
    {{knownAntibodies}}
    At the end, ask if the user want to measure how good they perform.
    """)
    String getAntibodies(@MemoryId int memoryId, @V("antigen") String antigen, @V("knownAntibodies") String knownAntibodies);
}