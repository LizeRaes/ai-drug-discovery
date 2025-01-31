package ma.devoxx.langchain4j.domain;

import lombok.*;
import ma.devoxx.langchain4j.state.CustomResearchProject;
import ma.devoxx.langchain4j.state.CustomResearchState;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationState {

    CustomResearchState customResearchState;
    CustomResearchProject customResearchProject;
}
