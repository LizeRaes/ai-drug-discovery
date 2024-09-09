package ma.devoxx.langchain4j.logging;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.logging.LogManager;

@ApplicationScoped
public class LogHandlerRegistration {

    @Inject
    WebSocketLogger webSocketLogger;

    public void onStart(@Observes StartupEvent ev) {
        System.out.println("Hey");
        DelegatingLogHandler handler = new DelegatingLogHandler(webSocketLogger);
        handler.setFormatter(new ExtLogFormatter());
        LogManager.getLogManager().getLogger("").addHandler(handler);
    }
}