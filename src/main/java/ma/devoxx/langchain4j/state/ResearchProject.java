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
        sb.append(", \nTarget Disease=").append(disease);
        sb.append(", \nAntigen Name=").append(antigenName);
        sb.append(", \nAntigen Sequence=").append(antigenSequence);
        if (!existingAntibodies.isEmpty()) {
            sb.append("\nExisting Antibodies found in literature:\n");
            for (Antibody proposedAntibody : existingAntibodies) {
                sb.append(proposedAntibody.toString());
            }
        }
        if (!newAntibodies.isEmpty()) {
            sb.append("\nNewly discovered Antibodies during this research:");
            for (Antibody proposedAntibody : newAntibodies) {
                sb.append(proposedAntibody.toString());
            }
        }
        sb.append("Methods used: innovative new approach using AI and ML models, amongst others OpenAI GPT-4, Anthropic Claude-3.5 and Google AlphaProteo");
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
            sb.append(antibody.toString()).append("\n\n---\n\n");
        }
        return sb.toString();
    }

    public String printExistingAntibodiesWithCDR() {
        StringBuilder sb = new StringBuilder();
        for (Antibody antibody : existingAntibodies) {
            if (antibody.cdrs != null) {
                sb.append(antibody.toString());
            }
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

    public void storeExistingCdrs(String antibodyName, String cdrs) {
        Antibody antibody = getAntibody(antibodyName);
        if (antibody == null) {
            antibody = new Antibody(antibodyName);
            existingAntibodies.add(antibody);
        }
        antibody.cdrs = cdrs;
    }

    public void storeNewCdrs(String antibodyName, String cdrs) {
        Antibody antibody = getAntibody(antibodyName);
        if (antibody == null) {
            antibody = new Antibody(antibodyName);
            newAntibodies.add(antibody);
        }
        antibody.cdrs = cdrs;
    }

    public Antibody[] getNewAntibodiesWithCdrs() {
        List<Antibody> newAntibodiesWithCdrs = new ArrayList<>();
        for (Antibody antibody : newAntibodies) {
            if (antibody.cdrs != null) {
                newAntibodiesWithCdrs.add(antibody);
            }
        }
        return newAntibodiesWithCdrs.toArray(new Antibody[0]);
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getAntigenName() {
        return antigenName;
    }

    public void setAntigenName(String antigenName) {
        this.antigenName = antigenName;
    }

    public String getAntigenSequence() {
        return antigenSequence;
    }

    public void setAntigenSequence(String antigenSequence) {
        this.antigenSequence = antigenSequence;
    }

    public List<Antibody> getExistingAntibodies() {
        return existingAntibodies;
    }

    public void setExistingAntibodies(List<Antibody> existingAntibodies) {
        this.existingAntibodies = existingAntibodies;
    }

    public List<Antibody> getNewAntibodies() {
        return newAntibodies;
    }

    public void setNewAntibodies(List<Antibody> newAntibodies) {
        this.newAntibodies = newAntibodies;
    }
}
