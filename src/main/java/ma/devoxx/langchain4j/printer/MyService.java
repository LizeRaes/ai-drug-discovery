package ma.devoxx.langchain4j.printer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

@ApplicationScoped
public class MyService {

    public void sendMessage(Session session, String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            System.out.println("Session is closed or null.");
        }
    }
}