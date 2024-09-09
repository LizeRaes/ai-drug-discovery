package ma.devoxx.langchain4j.dbs;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import ma.devoxx.langchain4j.aiservices.rag.CustomRetrievalAugmentor;
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

@Singleton
public class StartupService {

    CustomRetrievalAugmentor retrievalAugmentor;

    public void onStart(@Observes StartupEvent ev) {
        // initialize protein database
        try {
            PublicProteinDatabase db = new PublicProteinDatabase();
            db.dropAllTables();
            db.initializeProteinStructureDb();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to drop tables: " + e.getMessage());
        }
    }
}
