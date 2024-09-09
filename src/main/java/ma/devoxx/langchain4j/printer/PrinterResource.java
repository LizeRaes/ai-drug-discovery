package ma.devoxx.langchain4j.printer;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/print")
public class PrinterResource {

    Logger logger = Logger.getLogger(PrinterResource.class);

    @Inject
    MyService myService;

    @Inject
    MyWebSocket myWebSocket;

    public static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    @GET
    @Path("/message")
    public Response websocket() {
        // Retrieve the session by ID
        Session session = myWebSocket.getSessionById();

        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.withApiKey(OPENAI_API_KEY);

        String prompt = "Write a short funny french poem about developers and null-pointers, 10 lines maximum";

        logger.info("Nr of chars: " + prompt.length());
        logger.info("Nr of tokens: " + model.estimateTokenCount(prompt));

        model.generate(prompt, new StreamingResponseHandler<>() {

            @Override
            public void onNext(String token) {
                myService.sendMessage(session, token);
            }

            @Override
            public void onComplete(dev.langchain4j.model.output.Response<AiMessage> response) {
                logger.info("\n\nDone streaming");
            }

            @Override
            public void onError(Throwable error) {
                logger.info("Something went wrong: " + error.getMessage());
            }
        });

        return Response.ok("Message sent via WebSocket").build();
    }
}
