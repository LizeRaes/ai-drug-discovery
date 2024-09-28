package ma.devoxx.langchain4j.guardrails;

import dev.langchain4j.data.message.UserMessage;
import io.quarkiverse.langchain4j.guardrails.InputGuardrail;
import io.quarkiverse.langchain4j.guardrails.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

// tribute to the Quarkus team for this guardrail example
// see https://docs.quarkiverse.io/quarkus-langchain4j/dev/guardrails.html
@ApplicationScoped
public class NoBiologicalWeapon implements InputGuardrail {

    private final EvilUsageDetectionService service;

    public NoBiologicalWeapon(EvilUsageDetectionService service) {
        this.service = service;
    }

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        double result = service.isAttemptToBioWeapon(userMessage.singleText());
        if (result > 0.7) {
            Logger.getLogger(NoBiologicalWeapon.class.getName()).info("!!! Attempt to create biological weapon detected !!!");
            return failure("Prompt injection detected");
        }
        return success();
    }
}