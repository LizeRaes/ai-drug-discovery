package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.client.AlphaFoldProxy;
import ma.devoxx.langchain4j.client.AlphaFoldResponse;
import ma.devoxx.langchain4j.dbs.PublicProteinDatabase;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForCharacteristicsMeasurements implements Serializable {

    @Inject
    CustomResearchProject customResearchProject;
    @Inject
    CustomResearchState customResearchState;
    @RestClient
    AlphaFoldProxy alphaFoldProxy;

    private static final Logger logger = Logger.getLogger(ToolsForCharacteristicsMeasurements.class.getName());
    private static final Random random = new Random();

    @Tool
    public String getUniProtId(String antigenName) {
        logger.info("getUniProtId() called with antigenName="+ antigenName + "'");
        String antigenUniProtId = PublicProteinDatabase.getUniProtId(antigenName);
        logger.info("Antigen UniProt ID: " + antigenUniProtId);
        return antigenUniProtId;
    }

    @Tool
    public String getUrlToPbdStructureFile(String antigenUnitProtId) {
        logger.info("getUrlToPbdStructureFile() called with antigenUnitProtId="+ antigenUnitProtId + "'");
        // field to extract example: "pdbUrl": "https://alphafold.ebi.ac.uk/files/AF-Q26674-F1-model_v4.pdb"
        List<AlphaFoldResponse> response = alphaFoldProxy.getPrediction(antigenUnitProtId, "123");
        return response.get(0).getPdbUrl();
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