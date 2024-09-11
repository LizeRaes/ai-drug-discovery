package ma.devoxx.langchain4j.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.Metadata;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import jakarta.enterprise.context.ApplicationScoped;
import ma.devoxx.langchain4j.text.TextResource;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocuments;

@ApplicationScoped
public class CustomRetrievalAugmentor {
    RetrievalAugmentor retrievalAugmentor;
// TODO this seems to do lazy loading, only does it on the first message received from the WebSocket. find way to have it happen before, on application startup (otherwise too long wait after the first message)

    public CustomRetrievalAugmentor() {

        // initialize RAG
        // TODO make sure retrieved documents are logged to UI log
        // TODO add reranker

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
        // TODO add retriever for the SQLDatabase "Protein Sequence Database"
        // retrieverToDescription.put(SequenceDbContentRetriever, "Scientific literature on diseases, antigens and antibody solutions");
        QueryRouter queryRouter = new LanguageModelQueryRouter(chatModel, retrieverToDescription);

        this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();
    }

    private static ContentRetriever createContentRetriever(List<Document> documents) {

        // Here, we create and empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return EmbeddingStoreContentRetriever.from(embeddingStore);
        // TODO where to set maxresults (7), minscore (0.5?)
    }

    public static PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public static java.nio.file.Path toPath(String relativePath) {
        try {
            URL fileUrl = TextResource.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public RetrievalAugmentor getRetrievalAugmentor() {
        return retrievalAugmentor;
    }
}
