    package ma.devoxx.langchain4j.molecules;

    import java.util.Map;

    public class Antibody {
        public String antibodyName;
        public String cdrs;
        public Characteristics characteristics;

        public Antibody() {
        }

        public Antibody(String antibodyName) {
            this.antibodyName = antibodyName;
        }

        public Antibody(String antibodyName, String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
            this.antibodyName = antibodyName;
            setCharacteristics(bindingAffinity, specificity, stability, toxicity, immunogenicity);
        }

        public void setCharacteristics(String bindingAffinity, String specificity, String stability, String toxicity, String immunogenicity) {
            this.characteristics = new Characteristics(bindingAffinity, specificity, stability, toxicity, immunogenicity);
        }

        public void setCdrs(String cdrs) {
            this.cdrs = cdrs;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("**").append(antibodyName).append("**\n\n"); // Bold antibody name
            if (cdrs != null && !cdrs.isEmpty()) {
                sb.append("**CDRs:**\n");
                // Ensure proper formatting of CDRs as a list
                String formattedCdrs = cdrs.replace("\n", "\n- "); // Markdown bullet points
                sb.append("- ").append(formattedCdrs).append("\n\n");
            }
            sb.append(characteristics);
            return sb.toString();
        }

    }
