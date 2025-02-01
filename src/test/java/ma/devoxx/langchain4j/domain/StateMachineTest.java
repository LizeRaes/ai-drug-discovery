package ma.devoxx.langchain4j.domain;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.aiservices.KnownAntibodyFinder;
import ma.devoxx.langchain4j.aiservices.NewAntibodyFinder;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;
import ma.devoxx.langchain4j.tools.ToolsForKnownAntibodyFinder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@SuppressWarnings("unchecked")
class StateMachineTest {

    private static final Integer USER_ID = 1;
    private static final String DISEASE_NAME = "disease1";
    private static final String ANTIGEN_NAME = "EGFRvIII";

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

    @InjectMock
    NewAntibodyFinder newAntibodyFinder;

    ResearchState researchState;

    @BeforeEach
    void setup() {
        researchState = new ResearchState();
        Mockito.when(customResearchState.getResearchState()).thenReturn(researchState);
    }

    @Test
    void test_init() {
        stateMachine.init();

        assertEquals(ResearchState.Step.DEFINE_DISEASE, researchState.currentStep);
    }

    private void configureAntigenFinder(String response, boolean callTool) {
        ToolsForAntigenFinder toolsForAntigenFinder =
                new ToolsForAntigenFinder(customResearchProject, customResearchState);
        Mockito.when(antigenFinder.determineAntigenInfo(DISEASE_NAME))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    if (callTool) {
                        // EGFRvIII was found using RAG
                        String sequence = toolsForAntigenFinder.findSequenceForAntigen(ANTIGEN_NAME);
                        toolsForAntigenFinder.storeAntigenInfo(ANTIGEN_NAME, sequence);
                    }
                    return response;
                });
    }

    private void configureKnownAntibodyFinder(String answer, boolean callTools) {
        ToolsForKnownAntibodyFinder toolsForKnownAntibodyFinder =
                new ToolsForKnownAntibodyFinder(customResearchProject, customResearchState);
        Mockito.when(knownAntibodyFinder.getAntibodies(1, "EGFRvIII", DISEASE_NAME))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    if (callTools) {
                        toolsForKnownAntibodyFinder.storeAntibody("BsAbs");
                    }
                    return answer;
                });
    }

    private void configureNewAntibodyFinder(String answer) {
        Mockito.when(newAntibodyFinder.getAntibodies("Antigen: EGFRvIII (Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK\n" +
                "Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR\n" +
                "CDR-L1: \n" +
                "CDR-L2: \n" +
                "CDR-L3: \n" +
                "CDR-H1: \n" +
                "CDR-H2: \n" +
                "CDR-H3: \n" +
                ")", "")).thenReturn(answer);
    }

    @Test
    @DisplayName("should start the discovery (step 1)")
    void test_step1() {
        String firstMessage = "Hello";
        String expectedMessage = "Hello! How can I assist you today with antibody drug research?";
        configureDiseasePicker(firstMessage, expectedMessage, false);
        stateMachine.init();

        stateMachine.run(USER_ID, firstMessage, message -> assertEquals(expectedMessage, message));

        assertEquals(ResearchState.Step.DEFINE_DISEASE, researchState.currentStep);
    }

    @Test
    @DisplayName("should fail because antigen finder hasn't found an antigen")
    void test_step1_to_step2() {
        String firstMessage = "I want to cure disease1";
        String expectedMessage = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. " +
                "Current state: FIND_ANTIGEN";
        configureDiseasePicker(firstMessage, expectedMessage, true);
        configureAntigenFinder(expectedMessage, false);
        stateMachine.init();
        Consumer<Message> consumer = Mockito.mock(Consumer.class);

        stateMachine.run(USER_ID, firstMessage, consumer);

        //Mockito.verify(consumer).accept("Finding antigen info for disease1...");
        //Mockito.verify(consumer).accept(expectedMessage);
        assertEquals(ResearchState.Step.FIND_ANTIGEN, researchState.currentStep);
    }

    @Test
    @DisplayName("should fail because no antibodies have been found")
    void test_step1_to_step3_ko() {
        configureDiseasePicker(
                "I want to cure disease1",
                "Finding antigen info for disease1...", true);
        String expectedMessage = """
                 I found antigen : EGFRvIII
                                \s
                 with sequence : Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK
                                \s
                 Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR
                                \s
                 CDR-L1:\s
                                \s
                 CDR-L2:\s
                                \s
                 CDR-L3:\s
                                \s
                 CDR-H1:\s
                                \s
                 CDR-H2:\s
                                \s
                 CDR-H3:
                \s""";
        configureAntigenFinder(expectedMessage, true);
        configureKnownAntibodyFinder("Not found", false);
        stateMachine.init();
        Consumer<Message> consumer = Mockito.mock(Consumer.class);
        String expectedMessage2 = """
                I found antigen : EGFRvIII

                with sequence : Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK

                Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR

                CDR-L1:\s

                CDR-L2:\s

                CDR-L3:\s

                CDR-H1:\s

                CDR-H2:\s

                CDR-H3:\s

                """;

        stateMachine.run(USER_ID, "I want to cure disease1", consumer);

       // Mockito.verify(consumer).accept("Finding antigen info for disease1...");
       // Mockito.verify(consumer).accept(expectedMessage2);
       // Mockito.verify(consumer).accept("I'm searching the literature to find known antibodies for EGFRvIII...");
       // Mockito.verify(consumer).accept("Not found");
       // Mockito.verify(consumer).accept("ERROR: no antibodies were found for this antigen. Please reload the page and start over.");
        Mockito.verifyNoMoreInteractions(consumer);
        assertEquals(ResearchState.Step.FIND_KNOWN_ANTIBODIES, researchState.currentStep);
    }

    @Test
    @DisplayName("should found antibodies")
    void test_step1_to_step3() {
        String expectedMessage = getAntigenFoundMessage();
        configureDiseasePicker("I want to cure disease1", "Finding antigen info for disease1...", true);
        configureAntigenFinder(expectedMessage, true);
        configureKnownAntibodyFinder("found", true);
        Consumer<Message> consumer = Mockito.mock(Consumer.class);
        String expectedMessage2 = """
                I found antigen : EGFRvIII

                with sequence : Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK

                Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR

                CDR-L1:\s

                CDR-L2:\s

                CDR-L3:\s

                CDR-H1:\s

                CDR-H2:\s

                CDR-H3:\s

                """;

        stateMachine.init();
        stateMachine.run(USER_ID, "I want to cure disease1", consumer);

       // Mockito.verify(consumer).accept("Finding antigen info for disease1...");
       // Mockito.verify(consumer).accept(expectedMessage2);
       // Mockito.verify(consumer).accept("I'm searching the literature to find known antibodies for EGFRvIII...");
//        Mockito.verify(consumer).accept("""
//                We found the following antibodies:
//
//                BsAbs
//
//                CDRs\s
//                null
//
//                Characteristics\s
//                {Binding Affinity=bindingAffinity1}
//                {Specificity=specificity1}
//                {Stability=stability1}
//                {Toxicity=toxicity1}
//                {Immunogenicity=immunogenicity}
//
//
//
//                Which antibodies would you like to proceed with?""");
        Mockito.verifyNoMoreInteractions(consumer);
        assertEquals("EGFRvIII", customResearchProject.getResearchProject().antigenName);
        assertEquals(1, customResearchProject.getResearchProject().existingAntibodies.size());
        assertEquals(0, customResearchProject.getResearchProject().newAntibodies.size());
        assertEquals("""
                Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK
                Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR
                CDR-L1:\s
                CDR-L2:\s
                CDR-L3:\s
                CDR-H1:\s
                CDR-H2:\s
                CDR-H3:\s
                """, customResearchProject.getResearchProject().antigenSequence);
        assertEquals("disease1", customResearchProject.getResearchProject().disease);
        assertEquals(ResearchState.Step.FIND_KNOWN_CDRS, researchState.currentStep);
    }

    @Test
    void test_step1_to_step4() {
        String expectedMessage = getAntigenFoundMessage();
        configureDiseasePicker("I want to cure disease1", "Finding antigen info for disease1...", true);
        configureAntigenFinder(expectedMessage, true);
        configureKnownAntibodyFinder("found", true);
        configureNewAntibodyFinder("Hello");
        Consumer<Message> consumer = Mockito.mock(Consumer.class);

        customResearchState.getResearchState().moveToStep(ResearchState.Step.FIND_NEW_ANTIBODIES);
        stateMachine.run(USER_ID, "I want to cure disease1", consumer);

        //Mockito.verify(consumer).accept("Designing new antibodies based on known antibodies...");
        //Mockito.verify(consumer).accept("Hello");
        Mockito.verifyNoMoreInteractions(consumer);
        assertEquals(ResearchState.Step.FIND_NEW_ANTIBODIES, researchState.currentStep);
    }

    private void configureDiseasePicker(String query, String response, boolean callTool) {
        ToolsForDiseasePicker toolsForDiseasePicker = new ToolsForDiseasePicker(customResearchProject, customResearchState);
        Mockito.when(diseasePicker.answer(USER_ID, query))
                .thenAnswer((Answer<String>) invocationOnMock -> {
                    if (callTool) {
                        toolsForDiseasePicker.storeDiseaseName(DISEASE_NAME);
                    }
                    return response;
                });
    }

    private static @NotNull String getAntigenFoundMessage() {
        return """
                I found antigen : EGFRvIII
                               \s
                with sequence : Light chain: DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK
                               \s
                Heavy chain: QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR
                               \s
                CDR-L1:\s
                               \s
                CDR-L2:\s
                               \s
                CDR-L3:\s
                               \s
                CDR-H1:\s
                               \s
                CDR-H2:\s
                               \s
                CDR-H3:
               \s""";
    }
}