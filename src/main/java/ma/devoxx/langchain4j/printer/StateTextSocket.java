package ma.devoxx.langchain4j.printer;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import io.quarkus.websockets.next.*;
import jakarta.inject.Inject;
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

@WebSocket(path = "/my-websocket")
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

        // TODO watch out with the memory, check if we need another memory in later steps
        DiseasePicker diseasePicker = AiServices.builder(DiseasePicker.class)
                .chatLanguageModel(model)
                .chatMemory(customChatMemory.getChatMemory())
                .tools(new ToolsForDiseasePicker(customResearchProject))
                .build();

        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
            logger.info("IN STEP 1 (define target disease)");
            logger.info("STATE OF RESEARCH PROJECT BEFORE diseasePicker.answer(: " + customResearchProject.getResearchProject().toString());
            String answer = diseasePicker.answer(userMessage);
            logger.info("*** Model Answer ***: " + answer);
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
                // disease was not finally decided on yet
                // TODO fix websocket to not be streaming (because? something with the tools?)
                connection.sendTextAndAwait(answer);
                return;
            }
        }
        // else: model has set diseaseName and currentStep = 2 when decided on disease
        logger.info("STATE OF RESEARCH PROJECT AFTER diseasePicker.answer(: " + customResearchProject.getResearchProject().toString());
        connection.sendTextAndAwait("Stored disease " + customResearchProject.getResearchProject().disease + "\\\n");
        connection.sendTextAndAwait("Finding antigen info for " + customResearchProject.getResearchProject().disease + "\\\n");

        logger.info("STARTING STEP 2 (find antigen)");
        AntigenFinder antigenFinder = AiServices.builder(AntigenFinder.class)
                .chatLanguageModel(model)
                //.chatMemory(customChatMemory.getChatMemory())
                .retrievalAugmentor(customRetrievalAugmentor.getRetrievalAugmentor())
                //.retrievalAugmentor(getRetrievalAugmentor()) to use other documents
                .tools(new ToolsForAntigenFinder(customResearchProject.getResearchProject()))
                .build();

        String answer = antigenFinder.determineAntigenInfo(customResearchProject.getResearchProject().disease);
        logger.info("STATE OF RESEARCH PROJECT AFTER antigenFinder.determineAntigenInfo(: " + customResearchProject.getResearchProject().toString() + "\\");

        // if something went wrong with the antigenFinder
        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).
                startsWith("2")) {
            logger.info("UNEXPECTED STEP: " + ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()));
        }

        // this answer will not be shown to the user
        connection.sendTextAndAwait("Found antigen : " + customResearchProject.getResearchProject().antigenName+ "\\\n");
        connection.sendTextAndAwait("with sequence : " + customResearchProject.getResearchProject().antigenSequence+ "\\\n");

        // STEP 3
        logger.info("STARTING STEP 3 (find antibodies)");
        connection.sendTextAndAwait("Determining known antibodies for " + customResearchProject.getResearchProject().antigenName);
        // TODO write consecutive steps, with here and there human as a tool
    }

    @OnClose
    void onClose(WebSocketConnection connection) {
        final String sessionId = connection.id();

        // release some resources

        logger.info("Session closed, ID: {}", sessionId);
    }
}

