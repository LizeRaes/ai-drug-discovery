package ma.devoxx.langchain4j.text;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.devoxx.langchain4j.aiservices.AntigenFinder;
import ma.devoxx.langchain4j.aiservices.DiseasePicker;
import ma.devoxx.langchain4j.printer.MyService;
import ma.devoxx.langchain4j.printer.MyWebSocket;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.state.CustomChatMemory;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.ResearchStateMachine;
import ma.devoxx.langchain4j.tools.ToolsForAntigenFinder;
import ma.devoxx.langchain4j.tools.ToolsForDiseasePicker;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@Path("/statemessage")
public class StateTextResource {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(StateTextResource.class);
    Logger logger = Logger.getLogger(TextResource.class);

    private final String apiKey = System.getenv("OPENAI_API_KEY");

    @Inject
    MyWebSocket myWebSocket;

    @Inject
    MyService myService;

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

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response answer(String message) {
        logger.info(message);
        Session session = myWebSocket.getSessionById();

        // TODO watch out with the memory, check if we need another memory in later steps
        DiseasePicker diseasePicker = AiServices.builder(DiseasePicker.class)
                .chatLanguageModel(model)
                .chatMemory(customChatMemory.getChatMemory())
                .tools(new ToolsForDiseasePicker(customResearchProject))
                .build();

        if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
            logger.info("IN STEP 1 (define target disease)");
            logger.info("STATE OF RESEARCH PROJECT BEFORE diseasePicker.answer(: " + customResearchProject.getResearchProject().toString());
            String answer = diseasePicker.answer(message);
            logger.info("*** Model Answer ***: " + answer);
            if (ResearchStateMachine.getCurrentStep(customResearchProject.getResearchProject()).startsWith("1")) {
                // disease was not finally decided on yet
                // TODO fix websocket to not be streaming
                myService.sendMessage(session, answer);
                return Response.ok().build();
            }
        }
        // else: model has set diseaseName and currentStep = 2 when decided on disease
        logger.info("STATE OF RESEARCH PROJECT AFTER diseasePicker.answer(: " + customResearchProject.getResearchProject().toString());
        myService.sendMessage(session, "Stored disease " + customResearchProject.getResearchProject().disease + "\\\n");
        myService.sendMessage(session, "Finding antigen info for " + customResearchProject.getResearchProject().disease + "\\\n");

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
            return Response.ok().build();
        }

        // this answer will not be shown to the user
        myService.sendMessage(session, "Found antigen : " + customResearchProject.getResearchProject().antigenName+ "\\\n");
        myService.sendMessage(session, "with sequence : " + customResearchProject.getResearchProject().antigenSequence+ "\\\n");

        // STEP 3
        logger.info("STARTING STEP 3 (find antibodies)");
        myService.sendMessage(session,"Determining known antibodies for " + customResearchProject.getResearchProject().antigenName);
        // TODO write consecutive steps, with here and there human as a tool
        return Response.ok().build();

    }
}
