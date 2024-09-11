package ma.devoxx.langchain4j.dbs;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import ma.devoxx.langchain4j.rag.CustomRetrievalAugmentor;

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
