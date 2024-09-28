package ma.devoxx.langchain4j.printer;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.*;
import ma.devoxx.langchain4j.molecules.Antibody;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.state.ResearchState;
import ma.devoxx.langchain4j.state.CustomChatMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-state")
public class StateTextSocket {

    private static final Logger logger = LoggerFactory.getLogger(StateTextSocket.class);

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    private Integer userId;

    @Inject
    CustomChatMemory customChatMemory;

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

        // ************************** STEP 1 **************************
        if (customResearchState.getResearchState().currentStep == ResearchState.Step.DEFINE_DISEASE) {
            // TODO watch out with the memory, check if we need another memory in later steps
            // TODO build on every message? probably nicer solution

            logger.info("IN STEP 1 (define target disease)");
            String answer = diseasePicker.answer(userId, userMessage);
            logger.info("*** Model Answer ***: " + answer);
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.DEFINE_DISEASE) {
                // disease was not finally decided on yet
                connection.sendTextAndAwait(answer);
                return;
            }

            // else: model has set diseaseName and currentStep = 2 when decided on disease
            logger.info("******************** STEP 2 *********************");
            connection.sendTextAndAwait("Finding antigen info for " + customResearchProject.getResearchProject().disease + "...");

            answer = antigenFinder.determineAntigenInfo(customResearchProject.getResearchProject().disease);

            // if something went wrong with the antigenFinder
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_ANTIGEN) {
                String notification = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. Current state: " + customResearchState.getResearchState().currentStep;
                logger.info(notification);
                connection.sendTextAndAwait(notification);
                return;
            }

            connection.sendTextAndAwait("I found antigen : " + customResearchProject.getResearchProject().antigenName + "\n" +
                    "with sequence : " + customResearchProject.getResearchProject().antigenSequence);

            logger.info("******************** STEP 3 *********************");
            connection.sendTextAndAwait("I'm searching the literature to find known antibodies for " + customResearchProject.getResearchProject().antigenName);
            answer = knownAntibodyFinder.getAntibodies(userId, customResearchProject.getResearchProject().antigenName, customResearchProject.getResearchProject().disease);
            // if we didn't move to step 4, no antibodies were found
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_ANTIBODIES) {
                connection.sendTextAndAwait("UNEXPECTED STEP 3: failed at finding antibodies.");
                return;
            }
            // we ask the user's input at this point
            connection.sendTextAndAwait(answer);
            return;
        }

        if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
            logger.info("******************** STEP 4 *********************");
            // found antibodies, but only determine CDRs when decided which ones to proceed with
            String answer = cdrFinder.getCdrs(userId, userMessage);
            connection.sendTextAndAwait(answer); // TODO Lize test if loops correctly when unsure
            if(customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
                // still deciding on which antibodies to proceed with
                return;
            }
        }

        // else determine CDRs
        if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_NEW_ANTIBODIES) {
            logger.info("******************** STEP 5 *********************");
            connection.sendTextAndAwait("Designing new antibodies based on known antibodies...");
            // TODO actually we need confirmation here bcs it's costly calculations
            // TODO only pass the antibodies that actually have CDRs
            String answer = newAntibodyFinder.getAntibodies(userId, customResearchProject.getResearchProject().printAntigenInfo(), customResearchProject.getResearchProject().printAntibodiesWithCDR());
            connection.sendTextAndAwait(answer);
            return;
        }

        if (customResearchState.getResearchState().currentStep == ResearchState.Step.MEASURE_CHARACTERISTICS) {
            logger.info("******************** STEP 6 *********************");
            for(Antibody antibody : customResearchProject.getResearchProject().newAntibodies) {
                connection.sendTextAndAwait("Measuring characteristics for " + antibody.antibodyName + "...");
                connection.sendTextAndAwait(measureCharacteristics.measureCharacteristics(antibody.antibodyName, antibody.cdrs, customResearchProject.getResearchProject().antigenSequence));
            }
            logger.info("calling AiService ArticlePublisher with ResearchProject: " + customResearchProject.getResearchProject().toString() + "and authors: Mohamed Software and Lize Dev");
            articlePublisher.publishArticle(userId, userMessage, customResearchProject.getResearchProject().toString(), "Mohamed Software and Lize Dev");
            return;
        }
        connection.sendTextAndAwait("Thank you for using Ai Drug Discovery Researcher!");

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

