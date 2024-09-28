package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomResearchState {
    ResearchState researchState;

    public CustomResearchState() {
        researchState = new ResearchState();
    }

    public ResearchState getResearchState() {
        return researchState;
    }
}
