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
    public void storeAntibody(String antibodyName) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeAntibody() called with antibodyName='" + antibodyName + "'");
        customResearchProject.getResearchProject().existingAntibodies.add(new Antibody(antibodyName.toUpperCase(), "", "", "", "", ""));
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_KNOWN_CDRS;
    }

//    @Tool("")
//    public void storeAntibody(String antibodyName, String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
//        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeAntibody() called with antibodyName='" + antibodyName + "', bindingAffinity='" + bindingAffinity + "', specificity='" + specificity + "', stability='" + stability + "', toxicity='" + toxicity + "', immunogenicity='" + immunogenicity + "'");
//        customResearchProject.getResearchProject().existingAntibodies.add(new Antibody(antibodyName, bindingAffinity, specificity, stability, toxicity, immunogenicity));
//        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_KNOWN_CDRS;
//    }

     @Tool("")
    public void storeBindingAffinity(String antibodyName, String bindingAffinity) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeBindingAffinity() called with antibodyName='" + antibodyName + "', bindingAffinity='" + bindingAffinity + "'");
         customResearchProject.getResearchProject().existingAntibodies.stream()
                 .filter(antibody -> antibody.antibodyName.equalsIgnoreCase(antibodyName))
                 .findFirst()
                 .ifPresent(antibody -> antibody.characteristics.setBindingAffinity(bindingAffinity));
    }

    @Tool("")
    public void storeSpecificity(String antibodyName, String specificity) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeSpecificity() called with antibodyName='" + antibodyName + "', specificity='" + specificity + "'");
        customResearchProject.getResearchProject().existingAntibodies.stream()
                .filter(antibody -> antibody.antibodyName.equalsIgnoreCase(antibodyName))
                .findFirst()
                .ifPresent(antibody -> antibody.characteristics.setSpecificity(specificity));
    }

    @Tool("")
    public void storeStability(String antibodyName, String stability) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeStability() called with antibodyName='" + antibodyName + "', stability='" + stability + "'");
        customResearchProject.getResearchProject().existingAntibodies.stream()
                .filter(antibody -> antibody.antibodyName.equalsIgnoreCase(antibodyName))
                .findFirst()
                .ifPresent(antibody -> antibody.characteristics.setStability(stability));
    }

    @Tool("")
    public void storeToxicity(String antibodyName, String toxicity) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeToxicity() called with antibodyName='" + antibodyName + ", toxicity='" + toxicity + "'");
        customResearchProject.getResearchProject().existingAntibodies.stream()
                .filter(antibody -> antibody.antibodyName.equalsIgnoreCase(antibodyName))
                .findFirst()
                .ifPresent(antibody -> antibody.characteristics.setToxicity(toxicity));
    }

    @Tool("")
    public void storeImmunogenicity(String antibodyName, String immunogenicity) {
        Logger.getLogger(ToolsForKnownAntibodyFinder.class.getName()).info("storeImmunogenicity() called with antibodyName='" + antibodyName + "', immunogenicity='" + immunogenicity + "'");
        customResearchProject.getResearchProject().existingAntibodies.stream()
                .filter(antibody -> antibody.antibodyName.equalsIgnoreCase(antibodyName))
                .findFirst()
                .ifPresent(antibody -> antibody.characteristics.setImmunogenicity(immunogenicity));
    }

}