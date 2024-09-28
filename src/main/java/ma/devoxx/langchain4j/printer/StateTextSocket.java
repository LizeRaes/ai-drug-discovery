package ma.devoxx.langchain4j.printer;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.AntibodyFinder;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.CdrFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
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
    AntibodyFinder antibodyFinder;

    @Inject
    CdrFinder cdrFinder;

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
            connection.sendTextAndAwait("Finding antigen info for " + customResearchProject.getResearchProject().disease + "...\\n");

            answer = antigenFinder.determineAntigenInfo(customResearchProject.getResearchProject().disease);

            // if something went wrong with the antigenFinder
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_ANTIGEN) {
                String notification = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. Current state: " + customResearchState.getResearchState().currentStep;
                logger.info(notification);
                connection.sendTextAndAwait(notification);
                return;
            }

            connection.sendTextAndAwait("I found antigen : " + customResearchProject.getResearchProject().antigenName + "\\\n" +
                    "with sequence : " + customResearchProject.getResearchProject().antigenSequence + "\\\n");

            logger.info("******************** STEP 3 *********************");
            connection.sendTextAndAwait("I'm searching the literature to find known antibodies for " + customResearchProject.getResearchProject().antigenName);
            answer = antibodyFinder.getAntibodies(userId, customResearchProject.getResearchProject().antigenName);
            // if we didn't move to step 4, no antibodies were found
            if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_ANTIBODIES) {
                connection.sendTextAndAwait("UNEXPECTED STEP 3: failed at finding antibodies.");
                return;
            }
            // we ask the user's input at this point
            connection.sendTextAndAwait(answer);
            // TODO Lize allow to stay in this loop, for questions like 'I'm doubting between Necitumumab, cetuximab and 806 mAb'
            return;
        }

        if (customResearchState.getResearchState().currentStep == ResearchState.Step.FIND_KNOWN_CDRS) {
            logger.info("******************** STEP 4 *********************");
            String answer = cdrFinder.getCdrs(userId, userMessage);
            connection.sendTextAndAwait(answer);
            connection.sendTextAndAwait("TODO we got here, rest is TODO");
            // TODO implement option to loop here and discuss, vs move to step 5
            return;
        }

        // TODO write consecutive steps
        connection.sendTextAndAwait("TODO GOT HERE ALL AT THE END, WEIRD");

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

