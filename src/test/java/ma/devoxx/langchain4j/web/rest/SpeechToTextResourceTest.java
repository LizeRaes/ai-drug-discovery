package ma.devoxx.langchain4j.web.rest;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.AudioContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@Disabled
@QuarkusTest
class SpeechToTextResourceTest {

    @InjectMock
    ChatLanguageModel gemini;

    @Test
    void testGetTextEndpoint() throws IOException {
        String audio64 = Files.readString(Path.of("src/main/resources/audio.txt"));
        var systemMessage = SystemMessage.from("repeat the text from the audio message");
        var userMessage = new UserMessage(AudioContent.from(audio64, "audio/mp3"));
        Mockito.when(gemini.generate(systemMessage, userMessage))
                .thenReturn(Response.from(AiMessage.aiMessage("Hello from Quarkus REST")));

        given()
          .when().body(audio64).post("/audio")
          .then()
             .statusCode(200)
             .body(is("Hello from Quarkus REST"));
    }

}