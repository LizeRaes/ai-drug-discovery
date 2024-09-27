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
//
//    public void setLightChainSequence(String lightChainSequence) {
//        this.lightChainSequence = lightChainSequence;
//    }
//
//    public void setHeavyChainSequence(String heavyChainSequence) {
//        this.heavyChainSequence = heavyChainSequence;
//    }
//
//    public void setCDR_L1(String CDR_L1) {
//        this.CDR_L1 = CDR_L1;
//    }
//
//    public void setCDR_L2(String CDR_L2) {
//        this.CDR_L2 = CDR_L2;
//    }
//
//    public void setCDR_L3(String CDR_L3) {
//        this.CDR_L3 = CDR_L3;
//    }
//
//    public void setCDR_H1(String CDR_H1) {
//        this.CDR_H1 = CDR_H1;
//    }
//
//    public void setCDR_H2(String CDR_H2) {
//        this.CDR_H2 = CDR_H2;
//    }
//
//    public void setCDR_H3(String CDR_H3) {
//        this.CDR_H3 = CDR_H3;
//    }
}
