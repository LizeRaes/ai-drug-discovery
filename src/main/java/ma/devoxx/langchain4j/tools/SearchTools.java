package ma.devoxx.langchain4j.tools;

import ma.devoxx.langchain4j.dbs.PublicProteinDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SearchTools {

    private static final Logger logger = LoggerFactory.getLogger(SearchTools.class);

    static String findCDRsForAntibody(String antibodyName) {
        // TODO should move to SQLRetriever, no need to use tools for this one
        logger.info("Called findCDRsForAntibody() with antibodyName='{}'", antibodyName);
        PublicProteinDatabase db = new PublicProteinDatabase();
        String cdrs = db.retrieveSequences(antibodyName);
        logger.info("CDR(s) Found {}", cdrs);
        return cdrs;
    }

    static String findSequenceForAntigen(String antigenName) {
        logger.info("Called findSequenceForAntigen() with antigenName='{}'", antigenName);
        PublicProteinDatabase db = new PublicProteinDatabase();
        String cdrs = db.retrieveSequences(antigenName);
        logger.info("Found {}", cdrs);
        return cdrs;
    }
}

