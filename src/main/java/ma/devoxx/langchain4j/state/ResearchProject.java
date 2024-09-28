package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.molecules.Disease;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ResearchProject implements Serializable {

    //public Disease targetDisease;
    public String disease;
    public String antigenName;
    public String antigenSequence;
    public List<Antibody> existingAntibodies = new ArrayList<>();
    public List<Antibody> proposedAntibodies = new ArrayList<>();

    public ResearchProject() {
    }

    public ResearchProject(String diseaseName) {
        // TODO fix back to Disease object
        disease = diseaseName;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("===============================");
        sb.append("ResearchProject{\n");
        //sb.append("Step to execute: " + STEPS);
        sb.append(", \ntargetDisease=").append(disease);
        sb.append(", \nantigenName=").append(antigenName);
        sb.append(", \nantigenSequence=").append(antigenSequence);
        if(!proposedAntibodies.isEmpty()) {
            sb.append("\nproposedAntibodies=");
            for (Antibody proposedAntibody : proposedAntibodies) {
                sb.append(proposedAntibody.toString());
            }
        }
        sb.append("\n===============================");
        sb.append("\n}");
        return sb.toString();
    }

    public void setName(String name) {
        disease = name;
        //lastCompletedStep = STEP2;
    }

    public void setAntigenInfo(String antigenName, String antigenSequence) {
        this.antigenName = antigenName;
       this.antigenSequence = antigenSequence;
        //lastCompletedStep = STEP2;
    }

    public Antibody getAntibody(String antibodyName) {
        for (Antibody antibody : existingAntibodies) {
            if (antibody.antibodyName.equals(antibodyName)) {
                return antibody;
            }
        }
        return null;
    }

    public void storeCdrs(String antibodyName, String cdrs) {
        Antibody antibody = getAntibody(antibodyName);
        if (antibody != null) {
            antibody.cdrs = cdrs;
        }
    }
}
