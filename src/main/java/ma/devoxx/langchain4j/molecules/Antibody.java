package ma.devoxx.langchain4j.molecules;

public record Antibody (String antibodyName, String lightChainSequence, String heavyChainSequence, String CDR_L1, String CDR_L2, String CDR_L3, String CDR_H1, String CDR_H2, String CDR_H3, Characteristics characteristics) {
    public Antibody(String newAntibody) {
        this(newAntibody, null, null, null, null, null, null, null, null, null);
    }
}
