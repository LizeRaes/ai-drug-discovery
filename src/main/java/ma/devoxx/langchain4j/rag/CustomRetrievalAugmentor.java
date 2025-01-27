package ma.devoxx.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
//import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.dbs.SequenceDbContentRetriever;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@Startup
@ApplicationScoped
public class CustomRetrievalAugmentor {

    @Inject
    WebSearchEngine webSearchEngine;

    @Inject
    ScoringModel scoringModel;

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
            URL fileUrl = CustomRetrievalAugmentor.class.getClassLoader().getResource(relativePath);
            assert fileUrl != null;
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public RetrievalAugmentor getRetrievalAugmentor() {
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        // Default QueryRouter that uses all documents and websearch
        // QueryRouter queryRouter = new DefaultQueryRouter(literatureDocsRetreiver, webSearchContentRetriever);

        // OR Smart QueryRouter that uses all documents, websearch, and database
        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();

        // 1. Create document content retriever
        List<Document> documents = loadDocuments(toPath("docs"), glob("*.txt"));
        ContentRetriever literatureDocsRetriever = createContentRetriever(documents);
        retrieverToDescription.put(literatureDocsRetriever, "Scientific literature on diseases, antigens and antibody solutions");

        // 2. Create web search content retriever.
        ContentRetriever webSearchContentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(webSearchEngine)
                .build();
        retrieverToDescription.put(webSearchContentRetriever, "Web search");

        // 3. Create sql database content retriever.
         SqlDatabaseContentRetriever sequenceDbContentRetriever = new SequenceDbContentRetriever().get(chatModel);
         retrieverToDescription.put(sequenceDbContentRetriever, "Public protein database with sequences of all known proteins");

        QueryRouter queryRouter = new LanguageModelQueryRouter(chatModel, retrieverToDescription);

        // Create content aggregator
        ContentAggregator contentAggregator = new CustomReRankingContentAggregator(scoringModel, 0.3);

        // Create query compressor
        QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);

        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .queryTransformer(queryTransformer)
                .contentAggregator(contentAggregator)
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
