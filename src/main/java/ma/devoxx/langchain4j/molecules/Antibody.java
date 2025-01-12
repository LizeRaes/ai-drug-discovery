package ma.devoxx.langchain4j.molecules;

public class Antibody {
    public String antibodyName;
    public String cdrs;
    public Characteristics characteristics;

    public Antibody(String antibodyName) {
        this.antibodyName = antibodyName;
    }

    public Antibody(String antibodyName, String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
        this.antibodyName = antibodyName;
        setCharacteristics(bindingAffinity, specificity, stability, toxicity, immunogenicity);
    }

    public void setCharacteristics(String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
        this.characteristics = new Characteristics(bindingAffinity, specificity, stability, toxicity, immunogenicity);
    }

    public void setCdrs(String cdrs) {
        this.cdrs = cdrs;
    }

    public String toString() {
        return antibodyName +
                "\n\nCDRs \n" + cdrs +
                "\n\nCharacteristics \n" + characteristics;
    }
}
