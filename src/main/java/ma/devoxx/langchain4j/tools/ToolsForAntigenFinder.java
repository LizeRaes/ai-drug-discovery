package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.dbs.PublicProteinDatabase;
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
    public String findSequenceForAntigen(String antigenName) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("findSequenceForAntigen() called with antigenName='" + antigenName + "'");
        PublicProteinDatabase db = new PublicProteinDatabase();
        return db.retrieveSequences(antigenName);
    }

    @Tool("store antigen name and antigen sequence")
    public void storeAntigenInfo(String antigenName, @P("Light Chain and Heavy Chain only, markdown format")String antigenSequence) {
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info("storeAntigenInfo() called with antigenName='" + antigenName + "' and AntigenSequence='" + antigenSequence + "'");
        customResearchProject.getResearchProject().setAntigenInfo(antigenName, antigenSequence);
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_KNOWN_ANTIBODIES;
        Logger.getLogger(ToolsForAntigenFinder.class.getName()).info(customResearchProject.getResearchProject().toString());
    }
}