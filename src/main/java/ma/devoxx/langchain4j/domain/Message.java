package ma.devoxx.langchain4j.domain;

public class Message {

    private boolean user;
    private String text;

    public static Message userMessage(String text) {
        return new Message(true, text);
    }

    public static Message aiMessage(String text) {
        return new Message(false, text);
    }

    public Message() {
    }

    public Message(boolean user, String text) {
        this.user = user;
        this.text = text;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
