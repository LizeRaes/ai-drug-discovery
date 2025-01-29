package ma.devoxx.langchain4j.client;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ma.devoxx.langchain4j.tools.ToolsForCharacteristicsMeasurements;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ToolsForCharacteristicsMeasurementsTest {

    @Inject
    ToolsForCharacteristicsMeasurements tools;

    @Test
    void testGetUrlToPbdStructureFile() {
        // Valid UniProt ID for testing
        String antigenUniProtId = "P69905"; // Example UniProt ID for hemoglobin

        // Call the method and get the result
        String pdbUrl = tools.getUrlToPbdStructureFile(antigenUniProtId);

        // Validate the result
        assertNotNull(pdbUrl, "PDB URL should not be null");
        assertFalse(pdbUrl.isEmpty(), "PDB URL should not be empty");

        // Print the result for debugging (optional)
        System.out.println("PDB URL: " + pdbUrl);
    }
}
