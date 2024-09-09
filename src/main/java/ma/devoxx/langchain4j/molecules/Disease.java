package ma.devoxx.langchain4j.molecules;

public class Disease {
    // Public fields (you can also use private with getters/setters if needed)
    public String diseaseName;
    public String antigenName;
    public String antigenSequence;

    public Disease() {
    }

    // Constructor with just the disease name
    public Disease(String diseaseName) {
        this.diseaseName = diseaseName;

    }
}
