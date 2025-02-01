package ma.devoxx.langchain4j.web.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import ma.devoxx.langchain4j.Constants;
import ma.devoxx.langchain4j.dbs.StateSaver;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;
import ma.devoxx.langchain4j.web.ws.StateAssistantSocket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;


@Path("/history")
public class HistoryResource {

    private static final Logger log = Logger.getLogger(HistoryResource.class.getName());

    @Inject
    StateAssistantSocket stateAssistantSocket;

    @Inject
    CustomResearchProject project;
    @Inject
    CustomResearchState state;

    @Inject
    StateSaver stateSaver;

    @POST
    @Path("save/{name}")
    public void saveHistory(@PathParam("name") String name) {
        String directory = "src/main/resources/dbs/history";

        var messagesFileName = Constants.MAIN_MESSAGES_FILE_PATH;

        try {
            stateSaver.save(project, state,
                    Paths.get(directory.concat("/states/").concat(name).concat(".json")));
            Files.copy(messagesFileName, Paths.get(directory.concat("/messages/").concat(name).concat(".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("load/{name}")
    public void loadHistory(@PathParam("name") String name) {
        String directory = "src/main/resources/dbs/history";

        var messagesFileName = Constants.MAIN_MESSAGES_FILE_PATH;

        try {
            Files.copy(
                    Paths.get(directory.concat("/messages/").concat(name).concat(".json")),
                    messagesFileName);
            var saveState = stateSaver.load(Paths.get(directory.concat("/states/").concat(name).concat(".json")));
            state.setResearchState(saveState.getCustomResearchState().getResearchState());
            project.setResearchProject(saveState.getCustomResearchProject().getResearchProject());

            stateAssistantSocket.loadState();
        } catch (IOException e) {
            log.warning("No file found. " + e.getMessage());
        }
    }
}
