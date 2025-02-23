package ma.devoxx.langchain4j.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReciprocalRankFuser;
import dev.langchain4j.rag.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomReRankingContentAggregator implements ContentAggregator {

    private static final Logger logger = LoggerFactory.getLogger(CustomReRankingContentAggregator.class);

    public static final Function<Map<Query, Collection<List<Content>>>, Query> DEFAULT_QUERY_SELECTOR =
            (queryToContents) -> {
                if (queryToContents.size() > 1) {
                    throw new IllegalArgumentException(String.format("The 'queryToContents' contains %s queries, making the re-ranking ambiguous. " +
                            "Because there are multiple queries, it is unclear which one should be " +
                            "used for re-ranking. Please provide a 'querySelector' in the constructor/builder.", queryToContents.size()));
                }
                return queryToContents.keySet().iterator().next();
            };

    private final ScoringModel scoringModel;
    private final Function<Map<Query, Collection<List<Content>>>, Query> querySelector;
    //private final Double minScore;
    private final int topN;

    public CustomReRankingContentAggregator(ScoringModel scoringModel,
                                            int topN) {
        this.scoringModel = scoringModel;
        this.querySelector = DEFAULT_QUERY_SELECTOR;
        this.topN = topN;
//        this.minScore = minScore;
    }

    @Override
    public List<Content> aggregate(Map<Query, Collection<List<Content>>> queryToContents) {

        if (queryToContents.isEmpty()) {
            return List.of();
        }

        // Select a query against which all contents will be re-ranked
        Query query = querySelector.apply(queryToContents);

        // For each query, fuse all contents retrieved from different sources using that query
        Map<Query, List<Content>> queryToFusedContents = fuse(queryToContents);

        // Fuse all contents retrieved using all queries
        List<Content> fusedContents = ReciprocalRankFuser.fuse(queryToFusedContents.values());

        if (fusedContents.isEmpty()) {
            return fusedContents;
        }

        // Re-rank all the fused contents against the query selected by the query selector
        return reRankAndFilter(fusedContents, query);
    }

    protected Map<Query, List<Content>> fuse(Map<Query, Collection<List<Content>>> queryToContents) {
        Map<Query, List<Content>> fused = new LinkedHashMap<>();
        for (Query query : queryToContents.keySet()) {
            Collection<List<Content>> contents = queryToContents.get(query);
            fused.put(query, ReciprocalRankFuser.fuse(contents));
        }
        return fused;
    }

    protected List<Content> reRankAndFilter(List<Content> contents, Query query) {

        List<TextSegment> segments = contents.stream()
                .map(Content::textSegment)
                .peek(textSegment -> logger.info("SEGMENT FOR RERANKING : {}", textSegment.text()))
                .collect(Collectors.toList());

        List<Double> scores = scoringModel.scoreAll(segments, query.text()).content();

        Map<TextSegment, Double> segmentToScore = new HashMap<>();
        for (int i = 0; i < segments.size(); i++) {
            segmentToScore.put(segments.get(i), scores.get(i));
        }

        // if we want retained segments to have a minimum score
//        return segmentToScore.entrySet().stream()
//                .filter(entry -> minScore == null || entry.getValue() >= minScore)
//                .sorted(Map.Entry.<TextSegment, Double>comparingByValue().reversed())
//                .map(Map.Entry::getKey)
//                .peek(textSegment -> logger.info("aggregated segment : {}", textSegment.text()))
//                .map(Content::from)
//                .collect(Collectors.toList());

        // if we want to keep the top N segments
        return segmentToScore.entrySet().stream()
                .sorted(Map.Entry.<TextSegment, Double>comparingByValue().reversed()) // Sort by score descending
                .limit(topN) // Keep only the top N segments
                .peek(entry -> logger.info("RETAINED SEGMENT AFTER RERANKING (score {}: '{}'", entry.getValue(), entry.getKey().text()))
                .map(Map.Entry::getKey)
                .map(Content::from)
                .collect(Collectors.toList());
    }
}
