package ma.devoxx.langchain4j.web.rest;

import com.google.common.io.Files;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.extern.slf4j.Slf4j;
import ma.devoxx.langchain4j.Constants;
import ma.devoxx.langchain4j.web.ws.StateAssistantSocket;

import java.io.File;
import java.io.IOException;

@Slf4j
@Path("/history")
public class HistoryResource {

    @Inject
    StateAssistantSocket stateAssistantSocket;

    @POST
    @Path("save/{name}")
    public void saveHistory(@PathParam("name") String name) {
        String directory = "src/main/resources/dbs/history";

        String messagesFileName = Constants.MAIN_MESSAGES_FILE_PATH.toString();
        String stateFileName = "saved_state.json";

        try {
            Files.copy(
                    new File(messagesFileName),
                    new File(directory.concat("/messages/").concat(name).concat(".json")));
            Files.copy(
                    new File(stateFileName),
                    new File(directory.concat("/states/").concat(name).concat(".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("load/{name}")
    public void loadHistory(@PathParam("name") String name) {
        String directory = "src/main/resources/dbs/history";

        String messagesFileName = "messages.json";
        String stateFileName = "saved_state.json";

        try {
            Files.copy(
                    new File(directory.concat("/messages/").concat(name).concat(".json")),
                    new File(messagesFileName));
            Files.copy(
                    new File(directory.concat("/states/").concat(name).concat(".json")),
                    new File(stateFileName));

            stateAssistantSocket.init();
        } catch (IOException e) {
            log.warn("No file found. {}", e.getMessage());
        }
    }
}
