package ma.devoxx.langchain4j.audio;

import dev.langchain4j.data.message.AudioContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ma.devoxx.langchain4j.printer.StateTextSocket;
import org.jboss.logging.Logger;

@Path("/audio")
public class AudioResource {

    Logger logger = Logger.getLogger(AudioResource.class);

    private final String apiKey = System.getenv("GEMINI_TOKEN");
    private static final String MODEL_NAME = "gemini-1.5-flash-001";

    @Inject
    StateTextSocket stateTextSocket;

    //@Inject
   // MyService myService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello(String base64) {
        System.out.println(base64);
        //Session session = chatSocket.getSessionById();

        GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL_NAME)
                .logRequestsAndResponses(true)
                .build();

        logger.info("This is a log");

        logger.error("An error occurs", new IllegalArgumentException("This is a huge problem !"));

        var response = model.generate(new UserMessage(AudioContent.from(base64, "audio/mp3")));

       // myService.sendMessage(session, response.content().text());

        return Response.ok().build();
    }
}
