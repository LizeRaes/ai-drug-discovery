package ma.devoxx.langchain4j.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private boolean user;
    private String text;

    public static Message userMessage(String text) {
        return Message.builder().user(true).text(text).build();
    }

    public static Message aiMessage(String text) {
        return Message.builder().user(false).text(text).build();
    }
}
