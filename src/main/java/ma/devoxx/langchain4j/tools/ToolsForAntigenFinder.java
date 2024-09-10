package ma.devoxx.langchain4j.tools;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.state.ResearchProject;

import java.io.Serializable;

@ApplicationScoped
public class ToolsForAntigenFinder implements Serializable {
//    @Inject
//    ResearchProject myResearchProject;

    @Tool("store antigen info")
    void storeAntigenInfo(String AntigenName, String AntigenSequence) {
        // TODO
//        myResearchProject.setAntigenInfo(AntigenName,AntigenSequence);
    }
}