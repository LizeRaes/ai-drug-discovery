package ma.devoxx.langchain4j.logging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/logs")
@ApplicationScoped
public class LogWebSocket {

    private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    public void broadcast(String message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }
}