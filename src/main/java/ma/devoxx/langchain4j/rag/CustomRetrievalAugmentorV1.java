package ma.devoxx.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@Startup
@Named("step1")
@ApplicationScoped
public class CustomRetrievalAugmentorV1 {

    RetrievalAugmentor retrievalAugmentor;

    private static ContentRetriever createContentRetriever(List<Document> documents) {
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        for (Document document : documents) {
            DocumentSplitter splitter = DocumentSplitters.recursive(1000, 50);
            List<TextSegment> segments = splitter.split(document);
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);
        }
        return new EmbeddingStoreContentRetriever(embeddingStore, null, 7, 0.5);
    }

    public static PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = CustomRetrievalAugmentorV1.class.getClassLoader().getResource(relativePath);
            assert fileUrl != null;
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public RetrievalAugmentor getRetrievalAugmentor() {
        // 1. Create document content retriever
        List<Document> documents = loadDocuments(toPath("docs"), glob("*.txt"));
        ContentRetriever literatureDocsRetriever = createContentRetriever(documents);

        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(literatureDocsRetriever)
                .contentInjector(DefaultContentInjector.builder()
                        .promptTemplate(
                                PromptTemplate.from("{{userMessage}}\n" +
                                        "\n" +
                                        "if relevant to the question, you can use amongst others following information:\n" +
                                        "{{contents}}"))
                        .build())
                .build();
        return retrievalAugmentor;
    }
}
