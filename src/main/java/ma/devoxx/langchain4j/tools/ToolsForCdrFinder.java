package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForCdrFinder implements Serializable {

    CustomResearchProject customResearchProject;

    public ToolsForCdrFinder(CustomResearchProject customResearchProject) {
        this.customResearchProject = customResearchProject;
    }

    @Tool("")
    void storeCdrs(String antibodyName, String cdrs) {
        Logger.getLogger(ToolsForCdrFinder.class.getName()).info("storeCdrs() called with antibodyName='" + antibodyName + "', cdrs='" + cdrs + "'");
        customResearchProject.getResearchProject().storeCdrs(antibodyName, cdrs);
        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("STEP4")) {
            ResearchStateMachine.moveToNextStep(customResearchProject.getResearchProject());
        }
    }
}