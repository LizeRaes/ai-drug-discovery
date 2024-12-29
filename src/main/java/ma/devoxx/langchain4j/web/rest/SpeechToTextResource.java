package ma.devoxx.langchain4j.web.rest;

import dev.langchain4j.data.message.AudioContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/audio")
public class SpeechToTextResource {

    private static final Logger LOGGER = Logger.getLogger(SpeechToTextResource.class);
    private static final String API_KEY = System.getenv("GEMINI_TOKEN");
    private static final String MODEL_NAME = "gemini-1.5-flash-001";

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String getText(String base64) {
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(MODEL_NAME)
                .logRequestsAndResponses(true)
                .build();

        LOGGER.info("Gemini is converting audio to text...");
        var response = model.generate(
                SystemMessage.from("repeat the text from the audio message"),
                new UserMessage(AudioContent.from(base64, "audio/mp3")));

        String message = response.content().text();

        LOGGER.info("Audio convert. Text = " + message);

        return message;
    }
}
