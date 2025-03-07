package ma.devoxx.langchain4j.aiservices.supplier;

import dev.langchain4j.rag.RetrievalAugmentor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentorV2;

import java.util.function.Supplier;

@ApplicationScoped
public class CustomRetrievalAugmentorProvider implements Supplier<RetrievalAugmentor> {

    @Inject
    @Named("advanced")
    CustomRetrievalAugmentor augmentor;

    @Override
    public RetrievalAugmentor get() {
        return augmentor.getRetrievalAugmentor();
    }
}
