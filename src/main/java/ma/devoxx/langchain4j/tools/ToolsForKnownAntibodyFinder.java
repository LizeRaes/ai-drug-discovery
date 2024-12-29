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
public class ToolsForKnownAntibodyFinder implements Serializable {

    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;

    public ToolsForKnownAntibodyFinder(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

    @Tool("")
    public void storeAntibody(String antibodyName, String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeAntibody() called with antibodyName='" + antibodyName + "', bindingAffinity='" + bindingAffinity + "', specificity='" + specificity + "', stability='" + stability + "', toxicity='" + toxicity + "', immunogenicity='" + immunogenicity + "'");
        customResearchProject.getResearchProject().existingAntibodies.add(new Antibody(antibodyName, bindingAffinity, specificity, stability, toxicity, immunogenicity));
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_KNOWN_CDRS;
    }
}