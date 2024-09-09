package ma.devoxx.langchain4j.text;

import dev.langchain4j.service.TokenStream;

public interface Assistant {

    TokenStream answer(String query);
}