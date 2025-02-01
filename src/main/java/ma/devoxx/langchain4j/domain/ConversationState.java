package ma.devoxx.langchain4j.domain;

import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;

public class ConversationState {

    CustomResearchState customResearchState;
    CustomResearchProject customResearchProject;

    public ConversationState() {
        customResearchState = new CustomResearchState();
        customResearchProject = new CustomResearchProject();
    }

    public ConversationState(CustomResearchState customResearchState, CustomResearchProject customResearchProject) {
        this.customResearchState = customResearchState;
        this.customResearchProject = customResearchProject;
    }

    public CustomResearchState getCustomResearchState() {
        return customResearchState;
    }

    public void setCustomResearchState(CustomResearchState customResearchState) {
        this.customResearchState = customResearchState;
    }

    public CustomResearchProject getCustomResearchProject() {
        return customResearchProject;
    }

    public void setCustomResearchProject(CustomResearchProject customResearchProject) {
        this.customResearchProject = customResearchProject;
    }
}
