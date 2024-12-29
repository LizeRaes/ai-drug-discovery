package ma.devoxx.langchain4j.domain;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.aiservices.KnownAntibodyFinder;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class StateMachineTest {

    private static final Integer USER_ID = 1;

    @Inject
    StateMachine stateMachine;

    @Inject
    CustomResearchProject customResearchProject;

    @InjectMock
    DiseasePicker diseasePicker;

    @InjectMock
    AntigenFinder antigenFinder;

    @InjectMock
    KnownAntibodyFinder knownAntibodyFinder;

    @InjectMock
    CustomResearchState customResearchState;

    ResearchState researchState = new ResearchState();

    @BeforeEach
    void setup() {
        Mockito.when(customResearchState.getResearchState()).thenReturn(researchState);
    }

    @Test
    void test_init() {
        stateMachine.init();

        assertEquals(ResearchState.Step.DEFINE_DISEASE, researchState.currentStep);
    }

    @Test
    void test_step1() {
        String expectedMessage = "Hello! How can I assist you today with antibody drug research?";
        Mockito.when(diseasePicker.answer(1, "Hello")).thenReturn(expectedMessage);
        stateMachine.init();

        stateMachine.run(USER_ID, "Hello",
                message -> assertEquals(expectedMessage, message));

        assertEquals(ResearchState.Step.DEFINE_DISEASE, researchState.currentStep);
    }

    @Test
    void test_step1_to_step2() {
        ToolsForDiseasePicker toolsForDiseasePicker = new ToolsForDiseasePicker(customResearchProject, customResearchState);

        String expectedMessage = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. Current state: FIND_ANTIGEN";
        Mockito.when(diseasePicker.answer(1, "I want to cure disease1"))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    toolsForDiseasePicker.storeDiseaseName("disease1");
                    return "Finding antigen info for disease1...";
                });
        Mockito.when(antigenFinder.determineAntigenInfo("disease1")).thenReturn(expectedMessage);
        stateMachine.init();
        Consumer<String> consumer = Mockito.mock(Consumer.class);

        stateMachine.run(USER_ID, "I want to cure disease1", consumer);

        Mockito.verify(consumer).accept("Finding antigen info for disease1...");
        Mockito.verify(consumer).accept(expectedMessage);
        assertEquals(ResearchState.Step.FIND_ANTIGEN, researchState.currentStep);
    }

    @Test
    void test_step1_to_step3() {
        ToolsForDiseasePicker toolsForDiseasePicker = new ToolsForDiseasePicker(customResearchProject, customResearchState);
        ToolsForAntigenFinder toolsForAntigenFinder = new ToolsForAntigenFinder(customResearchProject, customResearchState);

        String expectedMessage = """
                I found antigen : EGFRvIII
                                
                with sequence : Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK
                                
                Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR
                                
                CDR-L1:\s
                                
                CDR-L2:\s
                                
                CDR-L3:\s
                                
                CDR-H1:\s
                                
                CDR-H2:\s
                                
                CDR-H3:
                """;
        Mockito.when(diseasePicker.answer(1, "I want to cure disease1"))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    toolsForDiseasePicker.storeDiseaseName("disease1");
                    return "Finding antigen info for disease1...";
                });
        Mockito.when(antigenFinder.determineAntigenInfo("disease1"))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    // EGFRvIII was found using RAG
                    String sequence = toolsForAntigenFinder.findSequenceForAntigen("EGFRvIII");
                    toolsForAntigenFinder.storeAntigenInfo("EGFRvIII", sequence);
                    return expectedMessage;
                });
        stateMachine.init();
        Consumer<String> consumer = Mockito.mock(Consumer.class);
        String expectedMessage2 = "I found antigen : EGFRvIII\n" +
                "\n" +
                "with sequence : Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK\n" +
                "\n" +
                "Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR\n" +
                "\n" +
                "CDR-L1: \n" +
                "\n" +
                "CDR-L2: \n" +
                "\n" +
                "CDR-L3: \n" +
                "\n" +
                "CDR-H1: \n" +
                "\n" +
                "CDR-H2: \n" +
                "\n" +
                "CDR-H3: \n" +
                "\n";
        Mockito.when(knownAntibodyFinder.getAntibodies(1, "EGFRvIII", "disease1"))
                .thenReturn("Not found");

        stateMachine.run(USER_ID, "I want to cure disease1", consumer);

        Mockito.verify(consumer).accept("Finding antigen info for disease1...");
        Mockito.verify(consumer).accept(expectedMessage2);
        Mockito.verify(consumer).accept("I'm searching the literature to find known antibodies for EGFRvIII...");
        Mockito.verify(consumer).accept("Not found");
        Mockito.verify(consumer).accept("ERROR: no antibodies were found for this antigen. Please reload the page and start over.");
        Mockito.verifyNoMoreInteractions(consumer);
        assertEquals(ResearchState.Step.FIND_KNOWN_ANTIBODIES, researchState.currentStep);
    }
}