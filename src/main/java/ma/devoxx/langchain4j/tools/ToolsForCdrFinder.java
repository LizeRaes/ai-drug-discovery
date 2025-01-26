package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.dbs.PublicProteinDatabase;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForCdrFinder implements Serializable {

    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;

    public ToolsForCdrFinder(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

    @Tool("find CDRs for antibody name")
    public String findCdrsForAntibody(String antibodyName) {
        Logger.getLogger(ToolsForCdrFinder.class.getName()).info("findCdrsForAntibody() called with antibodyName='" + antibodyName + "'");
        PublicProteinDatabase db = new PublicProteinDatabase();
        return db.retrieveSequences(antibodyName);
    }

    @Tool("")
    void storeCdrs(String antibodyName, String cdrs) {
        Logger.getLogger(ToolsForCdrFinder.class.getName()).info("storeCdrs() called with antibodyName='" + antibodyName + "', cdrs='" + cdrs + "'");
        customResearchProject.getResearchProject().storeExistingCdrs(antibodyName, cdrs);
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_NEW_ANTIBODIES;
    }

}