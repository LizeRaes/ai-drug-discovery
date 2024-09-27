package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorProvider;
import ma.devoxx.langchain4j.tools.ToolsForCdrFinder;

// TODO will SqlRetriever work?
//  TODO we can also only pass it in this step where we leave the docs and websearch out
@RegisterAiService(
        tools = ToolsForCdrFinder.class,
        retrievalAugmentor = CustomRetrievalAugmentorProvider.class)
public interface CdrFinder {
    @UserMessage("""
    {{usrMsg}}
    Provide me with the sequences of the chosen antibodies.
    Also store them using 
    """)
    String getCdrs(@MemoryId int memoryId, @V("usrMsg") String userMsg);
    // TODO is there memory here? we would need the former messages to figure out which are the antibodies chosen
    // @MemoryId is an option, to also add to AntibodyFinder then (that's the message we need)
}