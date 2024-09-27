package ma.devoxx.langchain4j.tools;

import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.molecules.Antibody;
import org.jboss.logging.Logger;

import java.util.List;

// TODO find appropriate services or make dummies
class SpecializedModels {

    static Antibody findNewCandidateAntibody(String antigenSequence, List<Antibody> knownAntibodies) {
        Logger.getLogger(SpecializedModels.class).info("Called findNewCandidateAntibody() with antigenSequence='" + antigenSequence + "', knownAntibodies=" + knownAntibodies);
        // TODO call specialized model that proposes new candidate antibody based on antigen sequence and already analyzed antibodies with their CDRs
        // or let another LLM that is more drug specialized do a proposal
        return new Antibody("NewAntibody");
    }

    static String findBindingAffinity(Antibody antibody, String antigenSequence) {
        Logger.getLogger(SpecializedModels.class).info("Called findBindingAffinity() with antibody=" + antibody + ", antigenSequence='" + antigenSequence + "'");
        // TODO call specialized model that finds binding affinity of antibody to antigen
        // or let another LLM that is more drug specialized do a proposal
        return "0.9";
    }

    static String findImmunogenicity(Antibody antibody, String antigenSequence) {
        Logger.getLogger(SpecializedModels.class).info("Called findImmunogenicity() with antibody=" + antibody + ", antigenSequence='" + antigenSequence + "'");
        // TODO call specialized model that finds immunogenicity of antibody to antigen
        // or let another LLM that is more drug specialized do a proposal
        return "0.1";
    }

    static String findToxicity(Antibody antibody, String antigenSequence) {
        Logger.getLogger(SpecializedModels.class).info("Called findToxicity() with antibody=" + antibody + ", antigenSequence='" + antigenSequence + "'");
        // TODO call specialized model that finds toxicity of antibody to antigen
        // or let another LLM that is more drug specialized do a proposal
        return "0.1";
    }

    // TODO add potential other targets that should not be targeted by the antibody
    static String findSpecificity(Antibody antibody, String antigenSequence) {
        Logger.getLogger(SpecializedModels.class).info("Called findSpecificity() with antibody=" + antibody + ", antigenSequence='" + antigenSequence + "'");
        // TODO call specialized model that finds specificity of antibody to antigen
        // or let another LLM that is more drug specialized do a proposal
        return "0.9";
    }

    static String findStability(Antibody antibody, String antigenSequence) {
        Logger.getLogger(SpecializedModels.class).info("Called findStability() with antibody=" + antibody + ", antigenSequence='" + antigenSequence + "'");
        // TODO call specialized model that finds stability of antibody to antigen
        // or let another LLM that is more drug specialized do a proposal
        return "0.9";
    }

}
