package ma.devoxx.langchain4j.printer;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.aiservices.AntibodyFinder;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.state.CustomChatMemory;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket(path = "/my-websocket-state")
public class StateTextSocket {

    private static final Logger logger = LoggerFactory.getLogger(StateTextSocket.class);


    private final String apiKey = System.getenv("OPENAI_API_KEY");

    @Inject
    CustomChatMemory customChatMemory;

    @Inject
    CustomResearchProject customResearchProject;

    @Inject
    CustomRetrievalAugmentor customRetrievalAugmentor;

    ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .logRequests(true)
            .logResponses(true)
            .build();

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        System.out.println("Session opened, ID: " + connection.id());

        // TODO init Ai Service here
    }

    @OnTextMessage
    public void onMessage(WebSocketConnection connection, String userMessage) throws Exception {
        System.out.println("Received message: " + userMessage);
        // retrieve user ID
        final String sessionId = connection.id();

        if (userMessage.equalsIgnoreCase("exit")) {
            return;
        }

        // ************************** STEP 1 **************************
        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
            // TODO watch out with the memory, check if we need another memory in later steps
            // TODO build on every message? probably nicer solution
            DiseasePicker diseasePicker = AiServices.builder(DiseasePicker.class)
                    .chatLanguageModel(model)
                    .chatMemory(customChatMemory.getChatMemory())
                    .tools(new ToolsForDiseasePicker(customResearchProject))
                    .build();
            logger.info("IN STEP 1 (define target disease)");
            // logger.info("STATE OF RESEARCH PROJECT BEFORE diseasePicker.answer(: " + customResearchProject.getResearchProject().toString());
            String answer = diseasePicker.answer(userMessage);
            logger.info("*** Model Answer ***: " + answer);
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
                // disease was not finally decided on yet
                connection.sendTextAndAwait(answer);
                return;
            }

            // else: model has set diseaseName and currentStep = 2 when decided on disease
            logger.info("******************** STEP 2 *********************");
            connection.sendTextAndAwait("Finding antigen info for " + customResearchProject.getResearchProject().disease + "...\\n");
            AntigenFinder antigenFinder = AiServices.builder(AntigenFinder.class)
                    .chatLanguageModel(model)
                    .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                    .tools(new ToolsForAntigenFinder(customResearchProject))
                    .build();

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
            AntibodyFinder antibodyFinderFromLiterature = AiServices.builder(AntibodyFinder.class)
                    .chatLanguageModel(model)
                    .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                    .build();
            // TODO we absolutely need querycompression for this to work properly and make sure the memory is clean
            answer = antibodyFinderFromLiterature.getAntibodies(customResearchProject.getResearchProject().antigenName);
            // TODO have we moved to step 4? give it the tool to do so then. handle both move and not move
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).
                    startsWith("4")) {
                connection.sendTextAndAwait("TODO we got to step 4 hurray");
            }
            connection.sendTextAndAwait(answer);
            return;

        }

        // ************************** STEP 3 **************************
        // TODO are we still in step 3?
        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {

        }


        // TODO write consecutive steps, with here and there human as a tool
        connection.sendTextAndAwait("GOT HERE, TODO REST");

    }

    @OnClose
    void onClose(WebSocketConnection connection) {
        final String sessionId = connection.id();

        // release some resources

        logger.info("Session closed, ID: {}", sessionId);
    }
}

