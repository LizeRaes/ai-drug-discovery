package ma.devoxx.langchain4j.aiservices.supplier;

import dev.langchain4j.rag.RetrievalAugmentor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;

import java.util.function.Supplier;

@ApplicationScoped
public class CustomRetrievalAugmentorProvider implements Supplier<RetrievalAugmentor> {

    @Inject
    CustomRetrievalAugmentor augmentor;

    @Override
    public RetrievalAugmentor get() {
        return augmentor.getRetrievalAugmentor();
    }
}
