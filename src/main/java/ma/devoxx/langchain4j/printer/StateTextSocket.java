package ma.devoxx.langchain4j.printer;

import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.AntibodyFinder;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.CdrFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@WebSocket(path = "/my-websocket-state")
public class StateTextSocket {

    private static final Logger logger = LoggerFactory.getLogger(StateTextSocket.class);

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    DiseasePicker diseasePicker;

    @Inject
    AntigenFinder antigenFinder;

    @Inject
    AntibodyFinder antibodyFinder;

    @Inject
    CdrFinder cdrFinder;

    private int userId;

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        userId = generateRandomInt();
        logger.info("Session opened, User ID: {}", userId);

        ResearchStateMachine.resetState(customResearchProject.getResearchProject());

        connection.sendTextAndAwait("Hi, I’m here to assist you with your antibody research today.");
    }

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, String userMessage) {
        System.out.println("Received message: " + userMessage);

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        // ************************** STEP 1 **************************
        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
            // TODO watch out with the memory, check if we need another memory in later steps
            // TODO build on every message? probably nicer solution

            logger.info("IN STEP 1 (define target disease)");
            // logger.info("STATE OF RESEARCH PROJECT BEFORE diseasePicker.answer(: " + customResearchProject.getResearchProject().toString());
            String answer = diseasePicker.answer(userId, userMessage);
            logger.info("*** Model Answer ***: " + answer);
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
                // disease was not finally decided on yet
                connection.sendTextAndAwait(answer);
                return;
            }

            // else: model has set diseaseName and currentStep = 2 when decided on disease
            logger.info("******************** STEP 2 *********************");
            connection.sendTextAndAwait("Finding antigen info for " + customResearchProject.getResearchProject().disease + "...\\n");

            answer = antigenFinder.determineAntigenInfo(customResearchProject.getResearchProject().disease);

            // if something went wrong with the antigenFinder
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).
                    startsWith("2")) {
                String notification = "UNEXPECTED STEP 2: failed at finding antigen name or sequence. Current state: " + ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject());
                logger.info(notification);
                connection.sendTextAndAwait(notification);
                return;
            }

            connection.sendTextAndAwait("I found antigen : " + customResearchProject.getResearchProject().antigenName + "\\\n" +
                    "with sequence : " + customResearchProject.getResearchProject().antigenSequence + "\\\n");

            logger.info("******************** STEP 3 *********************");
            connection.sendTextAndAwait("I'm searching the literature to find known antibodies for " + customResearchProject.getResearchProject().antigenName);
            answer = antibodyFinder.getAntibodies(1, customResearchProject.getResearchProject().antigenName);
            // if we didn't move to step 4, no antibodies were found
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).
                    startsWith("3")) {
                // TODO this shouldn't happen (or we continue without known antibodies)
                connection.sendTextAndAwait("why are we still in step 3?");
                return;
            }
            // TODO remove
            connection.sendTextAndAwait("TODO we got to step 4 hurray");
            // we ask the user's input at this point
            connection.sendTextAndAwait(answer); // TODO or a fixed one about which antibodies they want to get the CDRs of
            return;
        }

        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("4")) {
            logger.info("******************** STEP 4 *********************");
            String answer = cdrFinder.getCdrs(1, userMessage);
            connection.sendTextAndAwait(answer);
            connection.sendTextAndAwait("TODO we got here, rest is TODO");
            return;
        }

        // TODO write consecutive steps, with here and there human as a tool
        connection.sendTextAndAwait("GTODO OT HERE ALL AT THE END, WEIRD");

    }

    @OnClose
    void onClose(WebSocketConnection connection) {
        final String sessionId = connection.id();

        // release some resources

        logger.info("Session closed, ID: {}", sessionId);
    }

    private static int generateRandomInt() {
        Random random = new Random();
        return random.nextInt((100 - 1) + 1) + 1;
    }
}

