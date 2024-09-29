package ma.devoxx.langchain4j.web.ws;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.*;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-state")
public class StateAssistantSocket {

    private static final Logger logger = LoggerFactory.getLogger(StateAssistantSocket.class);

    private Integer userId;

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    CustomResearchState customResearchState;

    @Inject
    DiseasePicker diseasePicker;

    @Inject
    AntigenFinder antigenFinder;

    @Inject
    KnownAntibodyFinder knownAntibodyFinder;

    @Inject
    CdrFinder cdrFinder;

    @Inject
    NewAntibodyFinder newAntibodyFinder;

    @Inject
    CharacteristicsMeasurer measureCharacteristics;

    @Inject
    ArticlePublisher articlePublisher;

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        System.out.println("Session opened, ID: " + connection.id());
        customResearchState.getResearchState().moveToStep(ResearchState.Step.DEFINE_DISEASE);
        refreshUser();
        connection.sendTextAndAwait("Hi, I’m here to assist you with your antibody research today.");
    }

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, String userMessage) {
        System.out.println("Received message: " + userMessage);

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        try {
            // ************************** STEP 1 **************************
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.DEFINE_DISEASE) {
                logger.info("IN STEP 1 (define target disease)");
                String answer = diseasePicker.answer(userId, userMessage);
                logger.info("*** Model Answer ***: " + answer);
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.DEFINE_DISEASE) {
                    // disease was not finally decided on yet
                    connection.sendTextAndAwait(answer);
                    return;
                }
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_ANTIGEN) {
                // else: model has set diseaseName and currentStep = 2 when decided on disease
                logger.info("******************** STEP 2 *********************");
                connection.sendTextAndAwait("Finding antigen info for " + customResearchProject.getResearchProject().disease + "...");

                String answer = antigenFinder.determineAntigenInfo(customResearchProject.getResearchProject().disease);

                // if something went wrong with the antigenFinder
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_ANTIGEN) {
                    String notification = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. Current state: " + customResearchState.getResearchState().currentStep;
                    logger.info(notification);
                    connection.sendTextAndAwait(notification);
                    return;
                }

                String result = "I found antigen : " + customResearchProject.getResearchProject().antigenName + "\n" +
                        "with sequence : " + customResearchProject.getResearchProject().antigenSequence;
                connection.sendTextAndAwait(result.replace("\n", "\n\n"));
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_ANTIBODIES) {
                logger.info("******************** STEP 3 *********************");
                connection.sendTextAndAwait("I'm searching the literature to find known antibodies for " + customResearchProject.getResearchProject().antigenName + "...");
                String answer = knownAntibodyFinder.getAntibodies(userId, customResearchProject.getResearchProject().antigenName, customResearchProject.getResearchProject().disease);
                // if we didn't move to step 4, no antibodies were found
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_ANTIBODIES) {
                    connection.sendTextAndAwait(answer);
                    connection.sendTextAndAwait("ERROR: no antibodies were found for this antigen. Please reload the page and start over.");
                    return;
                }
                // we ask for the user's input at this point
                // TODO make layout better (in line with what comes out of LLM)
                connection.sendTextAndAwait("We found the following antibodies:\n\n"
                        + customResearchProject.getResearchProject().printAntibodies() +
                        "\n\nWhich antibodies would you like to proceed with?");
                return;
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
                logger.info("******************** STEP 4 *********************");
                String answer = cdrFinder.getCdrs(userId, userMessage);
                connection.sendTextAndAwait(answer);
                if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
                    // still deciding on which antibodies to proceed with
                    return;
                }
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_NEW_ANTIBODIES) {
                logger.info("******************** STEP 5 *********************");
                connection.sendTextAndAwait("Designing new antibodies based on known antibodies...");
                // we ask for confirmation here bcs it's costly calculations / experiments
                String answer = newAntibodyFinder.getAntibodies(userId, customResearchProject.getResearchProject().printAntigenInfo(), customResearchProject.getResearchProject().printExistingAntibodiesWithCDR());
                connection.sendTextAndAwait(answer);
                return;
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.MEASURE_CHARACTERISTICS) {
                logger.info("******************** STEP 6 *********************");
                for (Antibody antibody : customResearchProject.getResearchProject().getNewAntibodiesWithCdrs()) {
                    connection.sendTextAndAwait("Measuring characteristics for " + antibody.antibodyName + "...");
                    connection.sendTextAndAwait(measureCharacteristics.measureCharacteristics(antibody.antibodyName, antibody.cdrs, customResearchProject.getResearchProject().antigenSequence));
                }
                connection.sendTextAndAwait("All characteristics were measured. Do you want to publish the results of this research in Nature or in the New York Times?");
                return;
            }

            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FINISHED) {
                logger.info("calling AiService ArticlePublisher with ResearchProject: " + customResearchProject.getResearchProject().toString() + "and authors: Mohamed Software and Lize Dev");
                // give this AiService a clean memory
                String answer = articlePublisher.publishArticle(userId + 1, userMessage, customResearchProject.getResearchProject().toString(), "Mohamed Software and Lize Dev");
                connection.sendTextAndAwait(answer);
                return;
            }

            connection.sendTextAndAwait("We shouldn't get in this state. But thank you for using Ai Drug Discovery Researcher!");
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    @OnClose
    void onClose(WebSocketConnection connection) {
        final String sessionId = connection.id();

        // release some resources

        logger.info("Session closed, ID: {}", sessionId);
    }

    public void refreshUser() {
        userId = (int) (Math.random() * 1000) + 1;
    }
}

