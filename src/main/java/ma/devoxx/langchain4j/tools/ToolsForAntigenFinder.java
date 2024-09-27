package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;

import java.io.Serializable;
import java.util.logging.Logger;

//@ApplicationScoped
public class ToolsForAntigenFinder implements Serializable {
    //    @Inject
    ResearchProject myResearchProject;


    public ToolsForAntigenFinder(ResearchProject myResearchProject) {
        // TODO
        this.myResearchProject = myResearchProject;
    }

   /* @Tool("find sequence for antigen name")
    String findSequenceForAntigen(String antigenName) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("findSequenceForAntigen() called with antigenName='" + antigenName + "'");
        return SearchTools.findSequenceForAntigen(antigenName);
        // TODO handle if not found (maybe change to uppercase comparison if too many issues)
    }*/

    @Tool("store antigen name and antigen sequence")
    void storeAntigenInfo(String antigenName, String antigenSequence) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("storeAntigenInfo() called with AntigenName='" + antigenName + "' and AntigenSequence='" + antigenSequence + "'");
        myResearchProject.setAntigenInfo(antigenName, antigenSequence);
        ResearchStateMachine.moveToNextStep(myResearchProject);
    }
}