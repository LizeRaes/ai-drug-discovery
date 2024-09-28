package ma.devoxx.langchain4j.audio;

import dev.langchain4j.data.message.AudioContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/audio")
public class AudioResource {

    Logger logger = Logger.getLogger(AudioResource.class);

    private final String apiKey = System.getenv("GEMINI_TOKEN");
    private static final String MODEL_NAME = "gemini-1.5-flash-001";

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String getText(String base64) {
        GoogleAiGeminiChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(MODEL_NAME)
                .logRequestsAndResponses(true)
                .build();

        var response = model.generate(
                SystemMessage.from("repeat the text from the audio message"),
                new UserMessage(AudioContent.from(base64, "audio/mp3")));

        String message = response.content().text();

        logger.info("audio to text = " + message);

        return message;
    }
}
