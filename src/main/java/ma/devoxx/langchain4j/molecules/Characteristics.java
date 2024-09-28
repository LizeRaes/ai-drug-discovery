package ma.devoxx.langchain4j.molecules;

public class Characteristics {
    public String bindingAffinity;
    public String specificity;
    public String stability;
    public String toxicity;
    public String immunogenicity;

    public Characteristics(String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
        this.bindingAffinity = bindingAffinity;
        this.specificity = specificity;
        this.stability = stability;
        this.toxicity = toxicity;
        this.immunogenicity = immunogenicity;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{Binding Affinity=").append(bindingAffinity).append("}");
        sb.append("\n{Specificity=").append(specificity).append("}");
        sb.append("\n{Stability=").append(stability).append("}");
        sb.append("\n{Toxicity=").append(toxicity).append("}");
        sb.append("\n{Immunogenicity=").append(immunogenicity).append("}");
        return sb.toString();
    }
}
