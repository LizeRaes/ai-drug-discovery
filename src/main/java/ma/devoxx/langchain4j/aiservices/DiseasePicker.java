package ma.devoxx.langchain4j.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface DiseasePicker {
    @SystemMessage("""
    You help the user to choose the best disease for antibody research based on their questions.
    Once the user chooses a disease, you call storeDiseaseName with the disease name, and inform the user that their disease was stored and if they want to move on to the next step?
    """)
    TokenStream answer(String query);

    // TODO fix that minimum 5 characters query is required (gives error on 'yes'), and fix the HuggingFaceTokenizer log msg on startup (where does it come from?)
}