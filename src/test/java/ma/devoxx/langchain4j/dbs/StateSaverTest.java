package ma.devoxx.langchain4j.dbs;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.Constants;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.state.ResearchState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

@QuarkusTest
class StateSaverTest {

    @Inject
    StateSaver stateSaver;

    @Test
    void test() {
        var customResearchState = new CustomResearchState();
        ResearchState researchState = new ResearchState();
        researchState.moveToStep(ResearchState.Step.FIND_ANTIGEN);
        customResearchState.setResearchState(researchState);
        var customResearchProject = new CustomResearchProject();
        ResearchProject researchProject = new ResearchProject();
        researchProject.setAntigenName("antigen_name");
        customResearchProject.setResearchProject(researchProject);

        stateSaver.save(customResearchProject, customResearchState);

        Files.exists(Constants.MAIN_STATE_FILE_PATH);
    }

    @Test
    void test_load() {
        var conversationState = stateSaver.load("src/test/resources/saved_state.json");

        Assertions.assertEquals(ResearchState.Step.FIND_ANTIGEN,
                conversationState.getCustomResearchState().getResearchState().currentStep);
        Assertions.assertEquals("antigen_name",
                conversationState.getCustomResearchProject().getResearchProject().antigenName);
    }
}