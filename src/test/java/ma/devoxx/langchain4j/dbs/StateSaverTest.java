package ma.devoxx.langchain4j.dbs;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchProject;
import ma.devoxx.langchain4j.state.ResearchState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        Files.exists(Path.of(StateSaver.FILE_NAME));
    }

    @Test
    void test_load() {
        var conversationState = stateSaver.load("src/test/resources/saved_state.json");

        Assertions.assertEquals(ResearchState.Step.FIND_ANTIGEN,
                conversationState.getCustomResearchState().getResearchState().currentStep);
        Assertions.assertEquals("antigen_name",
                conversationState.getCustomResearchProject().getResearchProject().antigenName);
    }

    @BeforeAll
    static void teardown() throws IOException {
        Files.delete(Path.of(StateSaver.FILE_NAME));
    }
}