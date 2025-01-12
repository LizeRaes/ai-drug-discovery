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
        return "{Binding Affinity=" + bindingAffinity + "}" +
                "\n{Specificity=" + specificity + "}" +
                "\n{Stability=" + stability + "}" +
                "\n{Toxicity=" + toxicity + "}" +
                "\n{Immunogenicity=" + immunogenicity + "}";
    }
}
