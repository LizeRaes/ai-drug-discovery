package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.molecules.Disease;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchProject implements Serializable {
    // TODO Serializable can probably go
    private static final long serialVersionUID = 1L;

    final String STEPS = """
        1. Define target disease for antibody research (user input required), then printProjectState
        2. Find antigen name and sequence for target disease, the printProjectState
        3. Find known antibodies for target disease and their characteristics (binding affinity, specificity, stability, toxicity, immunogenicity) then printProjectState
        4. Find CDRs for the known antibodies, then printProjectState
        5. Find new candidate antibody based on antigen sequence and known antibodies, then printProjectState
        6. Determine characteristics of new candidate antibody (binding affinity, specificity, stability, toxicity, immunogenicity) (user permission required to proceed with calling those tool), then printProjectState
            """;

    final static String STEP1 = "1. Define target disease for antibody research (user input required), then printProjectState";
    final static String STEP2 = "2. Find antigen name and sequence for target disease, the printProjectState";
    final static String STEP3 = "3. Find known antibodies for target disease and their characteristics (binding affinity, specificity, stability, toxicity, immunogenicity) then printProjectState";
    final static String STEP4 = "4. Find CDRs for the known antibodies, then printProjectState";
    final static String STEP5 = "5. Find new candidate antibody based on antigen sequence and known antibodies, then printProjectState";
    final static String STEP6 = "6. Determine characteristics of new candidate antibody (binding affinity, specificity, stability, toxicity, immunogenicity) (user permission required to proceed with calling those tool), then printProjectState";
    final static String FINISHED = "7. Finished";

    public String lastCompletedStep = STEP1;
    public String currentStep = STEP1;
    public Disease targetDisease;
    public List<Antibody> proposedAntibodies = new ArrayList<>();

    public ResearchProject() {
    }

    public ResearchProject(String diseaseName) {
        targetDisease = new Disease(diseaseName);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResearchProject{\n");
        sb.append("Step to execute: " + STEPS);
        sb.append("\nlastCompletedStep=").append(lastCompletedStep);
        sb.append(", \ntargetDisease=").append(targetDisease);
        if(!proposedAntibodies.isEmpty()) {
            sb.append("\nproposedAntibodies=");
            for (Antibody proposedAntibody : proposedAntibodies) {
                sb.append(proposedAntibody.toString());
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

    public void setName(String name) {
        targetDisease = new Disease(name);
        lastCompletedStep = STEP1;
    }

    public void setAntigenInfo(String antigenName, String antigenSequence) {
        targetDisease.antigenName = antigenName;
        targetDisease.antigenSequence = antigenSequence;
        lastCompletedStep = STEP2;
    }
}
