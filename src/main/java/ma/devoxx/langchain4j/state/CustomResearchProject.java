package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Setter;

@Setter
@ApplicationScoped
public class CustomResearchProject {
    ResearchProject researchProject;

    public CustomResearchProject() {
        researchProject = new ResearchProject();
    }

    public ResearchProject getResearchProject() {
        return researchProject;
    }

}
