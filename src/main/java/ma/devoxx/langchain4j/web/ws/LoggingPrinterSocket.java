package ma.devoxx.langchain4j.web.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import ma.devoxx.langchain4j.logging.DelegatingLogHandler;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/logs")
@ApplicationScoped
public class LoggingPrinterSocket {

    private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    public void broadcast(DelegatingLogHandler.LoggerMessage message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(mapper.writeValueAsString(message));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }
}