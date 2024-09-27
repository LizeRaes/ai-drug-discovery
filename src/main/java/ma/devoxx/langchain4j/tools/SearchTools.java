package ma.devoxx.langchain4j.tools;

import ma.devoxx.langchain4j.dbs.PublicProteinDatabase;
import ma.devoxx.langchain4j.text.TextResource;
import org.jboss.logging.Logger;

class SearchTools {

    static String findCDRsForAntibody(String antibodyName) {
        // TODO should move to SQLRetriever, no need to use tools for this one
        Logger.getLogger(TextResource.class).info("Called findCDRsForAntibody() with antibodyName='" + antibodyName + "'");
        PublicProteinDatabase db = new PublicProteinDatabase();
        String cdrs = db.retrieveSequences(antibodyName);
        Logger.getLogger(TextResource.class).info("Found " + cdrs);
        return cdrs;
    }

    static String findSequenceForAntigen(String antigenName) {
        Logger.getLogger(TextResource.class).info("Called findSequenceForAntigen() with antigenName='" + antigenName + "'");
        PublicProteinDatabase db = new PublicProteinDatabase();
        String cdrs = db.retrieveSequences(antigenName);
        Logger.getLogger(TextResource.class).info("Found " + cdrs);
        return cdrs;
    }
}

