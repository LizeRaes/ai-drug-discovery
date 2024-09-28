package ma.devoxx.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.dbs.SequenceDbContentRetriever;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@ApplicationScoped
public class CustomRetrievalAugmentor {
    RetrievalAugmentor retrievalAugmentor;

    public CustomRetrievalAugmentor() {

        ChatLanguageModel chatModel = OpenAiChatModel.withApiKey(System.getenv("OPENAI_API_KEY"));

        List<Document> documents = loadDocuments(
                toPath("docs"), glob("*.txt"));

        // create web search content retriever.
        WebSearchEngine webSearchEngine = TavilyWebSearchEngine.builder()
                .apiKey(System.getenv("TAVILY_API_KEY"))
                .build();

        ContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .maxResults(5)
                .build();

        ContentRetriever literatureDocsRetreiver = createContentRetriever(documents);

        // Default QueryRouter that uses all documents and websearch
        // QueryRouter queryRouter = new DefaultQueryRouter(literatureDocsRetreiver, webSearchContentRetriever);

        // OR Smart QueryRouter that uses all documents, websearch, and database
        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(literatureDocsRetreiver, "Scientific literature on diseases, antigens and antibody solutions");
        retrieverToDescription.put(webSearchContentRetriever, "Web search");

        // Sql database retriever
        SqlDatabaseContentRetriever sequenceDbContentRetriever = new SequenceDbContentRetriever().get(chatModel);
        retrieverToDescription.put(sequenceDbContentRetriever, "protein database");

        QueryRouter queryRouter = new LanguageModelQueryRouter(chatModel, retrieverToDescription);

        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);

        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .queryTransformer(queryTransformer)
                .build();
    }

    private static ContentRetriever createContentRetrieverOld(List<Document> documents) {

        // Here, we create and empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return new EmbeddingStoreContentRetriever(embeddingStore, null, 7, 0.5);
    }

    private static ContentRetriever createContentRetriever(List<Document> documents) {
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        for (Document document : documents) {
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
            List<TextSegment> segments = splitter.split(document);
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);
        }
        return new EmbeddingStoreContentRetriever(embeddingStore, null, 7, 0.5);
    }

    public static PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public static java.nio.file.Path toPath(String relativePath) {
        try {
            URL fileUrl = CustomRetrievalAugmentor.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public RetrievalAugmentor getRetrievalAugmentor() {
        return retrievalAugmentor;
    }
}
