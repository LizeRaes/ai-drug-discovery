package ma.devoxx.langchain4j.tools;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;

import java.io.Serializable;
import java.util.logging.Logger;

@ApplicationScoped
public class ToolsForDiseasePicker implements Serializable {
    @Inject
    CustomResearchProject customResearchProject;

    @Tool("store the disease name")
    void storeDiseaseName(String name) {
     Logger.getLogger(ToolsForDiseasePicker.class.getName()).info("storeDiseaseName() called with name='" + name + "'");
     ResearchProject myResearchProject = customResearchProject.getResearchProject();
        Logger.getLogger(ToolsForDiseasePicker.class.getName()).info("current researchProject state:" + myResearchProject);
     myResearchProject.setName(name);
     ResearchStateMachine.moveToNextStep(myResearchProject);
    }
}