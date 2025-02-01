package ma.devoxx.langchain4j.aiservices;

import io.quarkiverse.langchain4j.scorer.junit5.AiScorer;
import io.quarkiverse.langchain4j.scorer.junit5.SampleLocation;
import io.quarkiverse.langchain4j.scorer.junit5.ScorerConfiguration;
import io.quarkiverse.langchain4j.testing.scorer.EvaluationReport;
import io.quarkiverse.langchain4j.testing.scorer.Parameters;
import io.quarkiverse.langchain4j.testing.scorer.Samples;
import io.quarkiverse.langchain4j.testing.scorer.Scorer;
import io.quarkiverse.langchain4j.testing.scorer.similarity.SemanticSimilarityStrategy;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import java.util.function.Function;

@QuarkusTest
@AiScorer
public class DiseasePickerTest {

    @Inject
    DiseasePicker diseasePicker;

    @Test
    void testAiService(@ScorerConfiguration(concurrency = 5) Scorer scorer, // The scorer instance, with concurrency set to 5
                       @SampleLocation("src/test/resources/samples.yaml") Samples<String> samples) { // The samples loaded from a YAML file

        // Define the function that will be evaluated
        // The parameters comes from the sample
        // The output of this function will be compared to the expected output in the samples
        Function<Parameters, String> function = parameters -> {
            return diseasePicker.answer(1, parameters.get(0));
        };

        EvaluationReport report = scorer.evaluate(samples, function, new SemanticSimilarityStrategy(0.8)); // The evaluation strategy
        Assert.assertTrue(report.score() >= 70); // Assert the score
    }
}