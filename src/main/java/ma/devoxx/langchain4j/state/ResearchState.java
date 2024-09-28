package ma.devoxx.langchain4j.state;

public class ResearchState {

    public enum Step {
        DEFINE_DISEASE("1. Define target disease for antibody research (user input required)"),
        FIND_ANTIGEN("2. Find antigen name and sequence for target disease, the printProjectState"),
        FIND_KNOWN_ANTIBODIES("3. Find known antibodies for target disease and their characteristics (binding affinity, specificity, stability, toxicity, immunogenicity) then printProjectState"),
        FIND_KNOWN_CDRS("4. Find CDRs for the known antibodies, then printProjectState"),
        FIND_NEW_ANTIBODIES("5. Find new candidate antibody based on antigen sequence and known antibodies, then printProjectState"),
        MEASURE_CHARACTERISTICS("6. Determine characteristics of new candidate antibody (binding affinity, specificity, stability, toxicity, immunogenicity) (user permission required to proceed with calling those tool)"),
        FINISHED("5. 7. Finished");

        private String description;

        Step(String description) {
            this.description = description;
        }

        Step step(int id) {
            return Step.values()[id];
        }

        public String getDescription() {
            return description;
        }
    }

    public Step currentStep = Step.DEFINE_DISEASE;

    public void moveToStep(Step step) {
        currentStep = step;
    }
}