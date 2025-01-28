package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.ClaudeAbGenerator;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForNewAntibodyFinder implements Serializable {

    @Inject
    CustomResearchProject customResearchProject;
    @Inject
    CustomResearchState customResearchState;
    @Inject
    ClaudeAbGenerator claudeAbGenerator;

    // sadly AlphaProteo is not available at the moment, so we return a dummy
    @Tool("propose new antibody for a given antigen sequence using AlphaProteo")
    String designNewAntibodyViaAlphaProteo(String antigenSequence) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("designNewAntibodyViaAlphaProteo() called with antigenSequence='" + antigenSequence + "'");
        // dummy
        return "CDR-L1\tCDR-L2\tCDR-L3\tCDR-H1\tCDR-H2\tCDR-H3\nDUMMY\tHGT\tVQYAQFFWT\tGYSSTSDFA\tTSYSGNT\tVTAGRGFPY";
    }

    // this would of course be another specialized model that makes an informed design based on results of previous antibodies
    // disclaimer: Claude 3.5 is not capable of this task, so consider the output a hallucination for demo purposes
    @Tool("propose new antibody for a given antigen sequence using Anthropic Claude")
    String designNewAntibodyViaAnthropicClaude(String antigenSequence, String previousAntibodies) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("designNewAntibodyViaAnthropicClaude() called with antigenSequence='" + antigenSequence + "'");
        try{
            String answer = claudeAbGenerator.generateNewAbSuggestion(antigenSequence, previousAntibodies);
            Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("Anthropic model returned: " + answer);
            return answer;
        } catch (Exception e) {
            Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("Error while calling Anthropic model: " + e.getMessage());
            return "no antibody could be designed because of an issue with the Anthropic model";
        }
    }

    @Tool("")
    void storeTheAntibody(String antibodyName) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("storeAntibody() called with antibodyName='" + antibodyName);
        customResearchProject.getResearchProject().newAntibodies.add(new Antibody(antibodyName));
        customResearchState.getResearchState().currentStep = ResearchState.Step.MEASURE_CHARACTERISTICS;
    }

    @Tool("")
    void storeNewCdrs(String antibodyName, String cdrs) {
        Logger.getLogger(ToolsForNewAntibodyFinder.class.getName()).info("storeNewCdrs() called with antibodyName='" + antibodyName + "', cdrs='" + cdrs + "'");
        customResearchProject.getResearchProject().storeNewCdrs(antibodyName, cdrs);
        customResearchState.getResearchState().currentStep = ResearchState.Step.MEASURE_CHARACTERISTICS;
    }
}