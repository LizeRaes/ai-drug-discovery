package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForCharacteristicsMeasurements implements Serializable {
    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;

    private static final Logger logger = Logger.getLogger(ToolsForCharacteristicsMeasurements.class.getName());
    private static final Random random = new Random();


    public ToolsForCharacteristicsMeasurements(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

    @Tool
    public String determineBindingAffinity(String CDRs, String antigenSequence) {
        logger.info("determineBindingAffinity() called with CDRs='" + CDRs + "', antigenSequence='" + antigenSequence + "'");
        // dummy to be replaced by a specialized model or lab experiment
        return generateHighRandomValue();
    }

    @Tool
    public String determineStability(String CDRs) {
        logger.info("determineStability() called with CDRs='" + CDRs + "'");
        // dummy to be replaced by a specialized model or lab experiment
        return generateHighRandomValue();
    }

    @Tool
    public String determineToxicity(String CDRs) {
        logger.info("determineToxicity() called with CDRs='" + CDRs + "'");
        // dummy to be replaced by a specialized model or lab experiment
        return generateLowRandomValue();
    }

    @Tool
    public String determineImmunogenicity(String CDRs) {
        logger.info("determineImmunogenicity() called with CDRs='" + CDRs + "'");
        // dummy to be replaced by a specialized model or lab experiment
        return generateHighRandomValue();
    }

    @Tool
    public String determineSpecificity(String CDRs, String antigenSequence) {
        logger.info("determineSpecificity() called with CDRs='" + CDRs + "', antigenSequence='" + antigenSequence + "'");
        // Example: dummy specific antigen alternatives for measurement
        String[] antigenAlternatives = {
                "AntigenA", "AntigenB", "AntigenC"
        };

        StringBuilder specificityReport = new StringBuilder();
        specificityReport.append("Specificity against antigen '").append(antigenSequence).append("': ").append(generateHighRandomValue()).append("\n");

        for (String alternative : antigenAlternatives) {
            specificityReport.append("Specificity against alternative antigen '").append(alternative).append("': ").append(generateHighRandomValue()).append("\n");
        }

        return specificityReport.toString();
    }

    @Tool("")
    void storeMeasurements(String antibodyName, String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
        logger.info("storeMeasurements() called with antibodyName='" + antibodyName + "', bindingAffinity='" + bindingAffinity + "', specificity='" + specificity + "', stability='" + stability + "', toxicity='" + toxicity + "', immunogenicity='" + immunogenicity + "'");
        customResearchProject.getResearchProject().getAntibody(antibodyName).setCharacteristics(bindingAffinity, specificity, stability, toxicity, immunogenicity);
        customResearchState.getResearchState().currentStep = ResearchState.Step.FINISHED;
    }

    private String generateHighRandomValue() {
        String[] levels = { "very high", "high"};
        return levels[random.nextInt(levels.length)];
    }

    private String generateLowRandomValue() {
        String[] levels = { "medium", "low", "very low"};
        return levels[random.nextInt(levels.length)];
    }
}