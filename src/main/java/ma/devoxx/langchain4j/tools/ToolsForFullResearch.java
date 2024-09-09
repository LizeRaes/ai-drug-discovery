package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.text.TextResource;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.List;

@ApplicationScoped
public class ToolsForFullResearch implements Serializable {
    @Inject
    ResearchProject myResearchProject;

    @Tool("Proposes new candidate antibody based on antigen sequence and already analyzed antibodies with their CDRs")
    Antibody findNewCandidateAntibody(String antigenSequence, List<Antibody> knownAntibodies) {
        return SpecializedModels.findNewCandidateAntibody(antigenSequence, knownAntibodies);
    }

    @Tool("Find binding affinity of antibody to antigen")
    String findBindingAffinity(Antibody antibody, String antigenSequence) {
        return SpecializedModels.findBindingAffinity(antibody, antigenSequence);
    }

    @Tool("Find immunogenicity of antibody to antigen")
    String findImmunogenicity(Antibody antibody, String antigenSequence) {
        return SpecializedModels.findImmunogenicity(antibody, antigenSequence);
    }

    @Tool("Find toxicity of antibody to antigen")
    String findToxicity(Antibody antibody, String antigenSequence) {
        return SpecializedModels.findToxicity(antibody, antigenSequence);
    }

    @Tool("Find specificity of antibody to antigen")
    String findSpecificity(Antibody antibody, String antigenSequence) {
        return SpecializedModels.findSpecificity(antibody, antigenSequence);
    }

    @Tool("Find stability of antibody to antigen")
    String findStability(Antibody antibody, String antigenSequence) {
        return SpecializedModels.findStability(antibody, antigenSequence);
    }

    @Tool("find CDRs for antibody name")
    String findCDRsForAntibody(String antibodyName) {
        return SearchTools.findCDRsForAntibody(antibodyName);
    }

    @Tool("find sequence for antigen name")
    String findSequenceForAntigen(String antigenName) {

        return SearchTools.findSequenceForAntigen(antigenName);
    }

    @Tool("print the state of the current project")
    String printProjectState() {
        return myResearchProject.toString();
    }

    @Tool("store the disease name")
    void storeDiseaseName(String name) {
        System.out.println("CALLED storeDiseaseName: " + name);
        Logger.getLogger(ToolsForFullResearch.class).info("CALLED storeDiseaseName: " + name);
       // TODO
        myResearchProject.setName(name);
    }

    @Tool("store antigen info")
    void storeAntigenInfo(String AntigenName, String AntigenSequence) {
        System.out.println("CALLED storeAntigenInfo: " + AntigenName + " " + AntigenSequence);
        Logger.getLogger(ToolsForFullResearch.class).info("CALLED storeAntigenInfo: " + AntigenName + " " + AntigenSequence);
        // TODO
        myResearchProject.setAntigenInfo(AntigenName,AntigenSequence);
    }

}
