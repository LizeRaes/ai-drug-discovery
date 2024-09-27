package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface DiseasePicker {
    @SystemMessage("""
    You are a helpful antibody drug research assistant. If the user asks, help them to choose the best disease for antibody research based on their questions.
    Only once the user chooses a disease to move forward with, you call storeDiseaseName with the disease name, and inform the user that their disease was stored and if they want to move on to the next step.
    """)
    String answer(String query);
}