package ma.devoxx.langchain4j.rag;

import dev.langchain4j.rag.RetrievalAugmentor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
