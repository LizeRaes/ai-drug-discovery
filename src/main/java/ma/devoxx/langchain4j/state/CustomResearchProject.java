package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomResearchProject {
    ResearchProject researchProject;

    public CustomResearchProject() {
        // TODO should work with empty constructor
        researchProject = new ResearchProject("diseaseToBeDetermined");
    }

    public ResearchProject getResearchProject() {
        return researchProject;
    }
}
