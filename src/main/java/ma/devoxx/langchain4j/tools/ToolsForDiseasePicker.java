package ma.devoxx.langchain4j.tools;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.state.ResearchState;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForDiseasePicker implements Serializable {
    CustomResearchProject customResearchProject;
    CustomResearchState customResearchState;

    public ToolsForDiseasePicker(CustomResearchProject customResearchProject, CustomResearchState customResearchState) {
        this.customResearchProject = customResearchProject;
        this.customResearchState = customResearchState;
    }

    @Tool("store the disease name")
    public void storeDiseaseName(String name) {
        Logger.getLogger(ToolsForDiseasePicker.class.getName()).info("storeDiseaseName() called with name='" + name + "'");
        ResearchProject myResearchProject = customResearchProject.getResearchProject();
        myResearchProject.setName(name);
        customResearchState.getResearchState().currentStep = ResearchState.Step.FIND_ANTIGEN;
        Logger.getLogger(ToolsForDiseasePicker.class.getName()).info(myResearchProject.toString());
    }
}