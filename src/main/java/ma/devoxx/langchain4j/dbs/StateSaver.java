package ma.devoxx.langchain4j.dbs;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import ma.devoxx.langchain4j.Constants;
import ma.devoxx.langchain4j.domain.ConversationState;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@ApplicationScoped
public class StateSaver {

    @Inject
    Jsonb jsonb;

    public void save(CustomResearchProject project, CustomResearchState state) {
        try {
            ConversationState conversationState = ConversationState.builder()
                    .customResearchProject(project)
                    .customResearchState(state)
                    .build();
            Files.writeString(Constants.MAIN_STATE_FILE_PATH, jsonb.toJson(conversationState),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConversationState load(String path) {
        try {
            return jsonb.fromJson(Files.readString(Path.of(path)), ConversationState.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
