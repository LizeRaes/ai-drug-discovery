package ma.devoxx.langchain4j.tools;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.state.ResearchProject;

import java.io.Serializable;

@ApplicationScoped
public class ToolsForDiseasePicker implements Serializable {
    @Inject
    ResearchProject myResearchProject;

 @Tool("store the disease name")
    void storeDiseaseName(String name) {
       // TODO
        myResearchProject.setName(name);
    }
}