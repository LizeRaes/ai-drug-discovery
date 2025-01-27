package ma.devoxx.langchain4j.state;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Setter;

@Setter
@ApplicationScoped
public class CustomResearchState {
    ResearchState researchState;

    public CustomResearchState() {
        researchState = new ResearchState();
    }

    public ResearchState getResearchState() {
        return researchState;
    }

    public void clear() {
        researchState = new ResearchState();
    }
}
