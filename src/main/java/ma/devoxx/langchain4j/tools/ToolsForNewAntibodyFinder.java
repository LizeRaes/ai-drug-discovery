package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForNewAntibodyFinder implements Serializable {
    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;


    public ToolsForNewAntibodyFinder(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

    // sadly AlphaProteo is not available at the moment, so we return a dummy
    @Tool("propose new antibody for a given antigen sequence using AlphaProteo")
    String designNewAntibodyViaAlphaProteo(String antigenSequence) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("designNewAntibodyViaAlphaProteo() called with antigenSequence='" + antigenSequence + "'");
        // TODO make a better dummy
        return "DUMMY_ANTIBODY";
    }

    // this would of course be another specialized model that makes an informed design based on results of previous antibodies
    // disclaimer: Claude 3.5 is not capable of this task, so consider the output a hallucination for demo purposes
    @Tool("propose new antibody for a given antigen sequence using AlphaProteo")
    String designNewAntibodyViaAnthropicClaude(String antigenSequence, String previousAntibodies) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("designNewAntibodyViaAlphaProteo() called with antigenSequence='" + antigenSequence + "'");
        // TODO call Claude with all that info and let it make a guess
        return "TODO CALL CLAUDE FOR REAL (tell this to the user - there's code missing here still)";
    }

    @Tool("")
    void storeTheAntibody(String antibodyName) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("storeAntibody() called with antibodyName='" + antibodyName);
        customResearchProject.getResearchProject().newAntibodies.add(new Antibody(antibodyName));
        customResearchState.getResearchState().currentStep = ResearchState.Step.MEASURE_CHARACTERISTICS;
    }

    @Tool("")
    void storeTheCdrs(String antibodyName, String cdrs) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("storeCdrs() called with antibodyName='" + antibodyName + "', cdrs='" + cdrs + "'");
        customResearchProject.getResearchProject().storeCdrs(antibodyName, cdrs);
        customResearchState.getResearchState().currentStep = ResearchState.Step.MEASURE_CHARACTERISTICS;
    }
}