package ma.devoxx.langchain4j.state;

import ma.devoxx.langchain4j.molecules.Antibody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ResearchProject implements Serializable {

    public String disease;
    public String antigenName;
    public String antigenSequence;
    public List<Antibody> existingAntibodies = new ArrayList<>();
    public List<Antibody> newAntibodies = new ArrayList<>();

    public ResearchProject() {
    }

    public ResearchProject(String diseaseName) {
        disease = diseaseName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("===============================");
        sb.append("ResearchProject{\n");
        sb.append(", \ntargetDisease=").append(disease);
        sb.append(", \nantigenName=").append(antigenName);
        sb.append(", \nantigenSequence=").append(antigenSequence);
        if (!newAntibodies.isEmpty()) {
            sb.append("\nproposedAntibodies=");
            for (Antibody proposedAntibody : newAntibodies) {
                sb.append(proposedAntibody.toString());
            }
        }
        sb.append("\n===============================");
        sb.append("\n}");
        return sb.toString();
    }

    public String printAntigenInfo() {
        return "Antigen: " + antigenName + " (" + antigenSequence + ")";
    }

    public String printAntibodies() {
        StringBuilder sb = new StringBuilder();
        for (Antibody antibody : existingAntibodies) {
            sb.append(antibody.toString());
        }
        return sb.toString();
    }

    public void setName(String name) {
        disease = name;
    }

    public void setAntigenInfo(String antigenName, String antigenSequence) {
        this.antigenName = antigenName;
        this.antigenSequence = antigenSequence;
    }

    public Antibody getAntibody(String antibodyName) {
        for (Antibody antibody : existingAntibodies) {
            if (antibody.antibodyName.equals(antibodyName)) {
                return antibody;
            }
        }
        for (Antibody antibody : newAntibodies) {
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
