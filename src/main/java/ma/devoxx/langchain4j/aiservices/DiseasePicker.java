package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface DiseasePicker {
    @SystemMessage("""
    You help the user to choose the best disease for antibody research based on their questions.
    Once the user choose a disease, you call storeDiseaseName with the disease name.
    """)
    TokenStream answer(String query);
}