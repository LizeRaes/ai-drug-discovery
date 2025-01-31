package ma.devoxx.langchain4j.logging;

public class LoggerMessage {

    private final String color;
    private final String message;

    public LoggerMessage(String color, String message) {
        this.color = color;
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public String getMessage() {
        return message;
    }
}