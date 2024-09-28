package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForAntigenFinder implements Serializable {
    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;


    public ToolsForAntigenFinder(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

   @Tool("find sequence for antigen name")
    String findSequenceForAntigen(String antigenName) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("findSequenceForAntigen() called with antigenName='" + antigenName + "'");
        return SearchTools.findSequenceForAntigen(antigenName);
        // TODO Lize handle if not found (maybe change to uppercase comparison if too many issues?)
    }

    @Tool("store antigen name and antigen sequence")
    void storeAntigenInfo(String antigenName, String antigenSequence) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("storeAntigenInfo() called with antigenName='" + antigenName + "' and AntigenSequence='" + antigenSequence + "'");
        customResearchProject.getResearchProject().setAntigenInfo(antigenName, antigenSequence);
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_KNOWN_ANTIBODIES;
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info(customResearchProject.getResearchProject().toString());
    }
}