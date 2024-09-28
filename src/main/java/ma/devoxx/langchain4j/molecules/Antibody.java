package ma.devoxx.langchain4j.molecules;

public class Antibody {
    public String antibodyName;
    public String cdrs;
//    public String lightChainSequence;
//    public String heavyChainSequence;
//    public String CDR_L1;
//    public String CDR_L2;
//    public String CDR_L3;
//    public String CDR_H1;
//    public String CDR_H2;
//    public String CDR_H3;
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
        StringBuilder sb = new StringBuilder();
        sb.append("\nAntibody{");
        sb.append("\n\tantibodyName=").append(antibodyName);
        sb.append("\n\tcdrs=").append(cdrs);
        sb.append("\n\tcharacteristics=").append(characteristics);
        sb.append("\n}");
        return sb.toString();
    }
}
