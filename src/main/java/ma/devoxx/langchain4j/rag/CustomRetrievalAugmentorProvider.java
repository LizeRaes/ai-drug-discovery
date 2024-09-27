package ma.devoxx.langchain4j.rag;

import dev.langchain4j.rag.RetrievalAugmentor;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Supplier;

@ApplicationScoped
public class CustomRetrievalAugmentorProvider implements Supplier<RetrievalAugmentor> {

    @Override
    public RetrievalAugmentor get() {
        CustomRetrievalAugmentor augmentor = new CustomRetrievalAugmentor();
        return augmentor.getRetrievalAugmentor();
    }
}
