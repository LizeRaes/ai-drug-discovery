package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomResearchProject {
    ResearchProject researchProject;

    public CustomResearchProject() {
        researchProject = new ResearchProject();
    }

    public ResearchProject getResearchProject() {
        return researchProject;
    }

    public void clear() {
        researchProject = new ResearchProject();
    }

    public void setResearchProject(ResearchProject researchProject) {
        this.researchProject = researchProject;
    }
}
