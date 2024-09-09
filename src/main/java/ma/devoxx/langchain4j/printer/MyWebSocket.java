package ma.devoxx.langchain4j.printer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/my-websocket")
@ApplicationScoped
public class MyWebSocket {

    @Inject
    MyService myService;

    // A thread-safe map to store sessions, keyed by session ID
    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        // Store the session in the map
        sessions.put("0", session);

        System.out.println(session.getId());
       // myService.sendMessage(session, "Hello from Quarkus!");
    }

    public Session getSessionById() {
        // Retrieve the session by its ID
        return sessions.get("0");
    }

    public void removeSessionById(String sessionId) {
        // Remove the session when it's no longer needed (e.g., on close)
        sessions.remove(sessionId);
    }
}

