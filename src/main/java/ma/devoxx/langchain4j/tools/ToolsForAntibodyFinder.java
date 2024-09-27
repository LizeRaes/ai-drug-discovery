package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForAntibodyFinder implements Serializable {

    CustomResearchProject customResearchProject;

    public ToolsForAntibodyFinder(CustomResearchProject customResearchProject) {
        this.customResearchProject = customResearchProject;
    }

    @Tool("")
    void storeAntibody(String antibodyName, String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
        Logger.getLogger(ToolsForAntibodyFinder.class.getName()).info("storeAntibody() called with antibodyName='" + antibodyName + "', bindingAffinity='" + bindingAffinity + "', specificity='" + specificity + "', stability='" + stability + "', toxicity='" + toxicity + "', immunogenicity='" + immunogenicity + "'");
        customResearchProject.getResearchProject().existingAntibodies.add(new Antibody(antibodyName, bindingAffinity, specificity, stability, toxicity, immunogenicity));
        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("STEP3")) {
            ResearchStateMachine.moveToNextStep(customResearchProject.getResearchProject());
        }
    }
}