package ma.devoxx.langchain4j.dbs;

import java.sql.*;
import java.io.File;
import java.util.logging.Logger;

public class PublicProteinDatabase {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/dbs/protein_structure.db";

    public static void main(String[] args) {
        PublicProteinDatabase db = new PublicProteinDatabase();
        db.initializeProteinStructureDb();
        System.out.println(db.retrieveSequences("Cetuximab"));
        System.out.println();
        System.out.println();
        System.out.println(db.retrieveSequences("EGFRvIII"));

    }

    public void dropAllTables() {
        File dbFile = new File("src/main/resources/dbs/protein_structure.db");

        // Check if the database file exists
        if (!dbFile.exists()) {
            System.out.println("Database file does not exist.");
            return;
        }

        String selectTablesSQL = "SELECT name FROM sqlite_master WHERE type='table'";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectTablesSQL)) {

            while (rs.next()) {
                String tableName = rs.getString("name");
                String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;
                stmt.execute(dropTableSQL);
                System.out.println("Dropped table: " + tableName);
            }

            System.out.println("All tables dropped.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initializeProteinStructureDb() {
        String createTableSQL =
                "CREATE TABLE IF NOT EXISTS proteins ( " +
                        "protein_name TEXT PRIMARY KEY, " +
                        "light_chain TEXT, " +
                        "heavy_chain TEXT, " +
                        "CDR_L1 TEXT, " +
                        "CDR_L2 TEXT, " +
                        "CDR_L3 TEXT, " +
                        "CDR_H1 TEXT, " +
                        "CDR_H2 TEXT, " +
                        "CDR_H3 TEXT " +
                        ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);

            // Insert protein data
            String insertDataSQL =
                    "INSERT INTO proteins (protein_name, light_chain, heavy_chain, CDR_L1, CDR_L2, CDR_L3, CDR_H1, CDR_H2, CDR_H3) " +
                            "VALUES " +
                            "('806 mAb', 'DILMTQSPSSMSVSLGDTVSITCHSSQDINSNIGWLQQKPGKSFKGLIYHGTNLDDEVPSRFSGSGSGADYSLTISSLESEDFADYYCVQYAQFPWTFGGGTKLEIKRADAAPTVSIFPPSSEQLTSGGASVVCFLNNFYPKDINVKWKIDGSERQNGVLNSWTDQDSKDSTYSMSSTLTLTKDEYERHNSYTCEATHKTSTSPIVKSFNRNEC', " +
                            "'DVQLQESGPSLVKPSQSLSLTCTVTGYSITSDFAWNWIRQFPGNKLEWMGYISYSGNTRYNPSLKSRISITRDTSKNQFFLQLNSVTIEDTATYYCVTAGRGFPYWGQGTLVTVSAAKTTPPSVYPLAPGCGDTTGSSVTLGCLVKGYFPESVTVTWNSGSLSSSVHTFPALLQSGLYTMSSSVTVPSSTWPSETVTCSVAHPASSTTVDKKLEPS', " +
                            "'QDINSN', 'HGT', 'VQYAQFPWT', 'GYSITSDFA', 'ISYSGNT', 'VTAGRGFPY'), " +

                            "('mAb 806', 'DILMTQSPSSMSVSLGDTVSITCHSSQDINSNIGWLQQKPGKSFKGLIYHGTNLDDEVPSRFSGSGSGADYSLTISSLESEDFADYYCVQYAQFPWTFGGGTKLEIKRADAAPTVSIFPPSSEQLTSGGASVVCFLNNFYPKDINVKWKIDGSERQNGVLNSWTDQDSKDSTYSMSSTLTLTKDEYERHNSYTCEATHKTSTSPIVKSFNRNEC', " +
                            "'DVQLQESGPSLVKPSQSLSLTCTVTGYSITSDFAWNWIRQFPGNKLEWMGYISYSGNTRYNPSLKSRISITRDTSKNQFFLQLNSVTIEDTATYYCVTAGRGFPYWGQGTLVTVSAAKTTPPSVYPLAPGCGDTTGSSVTLGCLVKGYFPESVTVTWNSGSLSSSVHTFPALLQSGLYTMSSSVTVPSSTWPSETVTCSVAHPASSTTVDKKLEPS', " +
                            "'QDINSN', 'HGT', 'VQYAQFPWT', 'GYSITSDFA', 'ISYSGNT', 'VTAGRGFPY'), " +

                            "('Cetuximab', 'DILLTQSPVILSVSPGERVSFSCRASQSIGTNIHWYQQRTNGSPRLLIKYASESISGIPSRFSGSGSGTDFTLSINSVESEDIADYYCQQNNNWPTTFGAGTKLELKRTVAAPSVFIFPPSDEQLKSGTASVVCLLNNFYPREAKVQWKVDNALQSGNSQESVTEQDSKDSTYSLSSTLTLSKADYEKHKVYACEVTHQGLSSPVTKSFNRGA', " +
                            "'QVQLKQSGPGLVQPSQSLSITCTVSGFSLTNYGVHWVRQSPGKGLEWLGVIWSGGNTDYNTPFTSRLSINKDNSKSQVFFKMNSLQSNDTAIYYCARALTYYDYEFAYWGQGTLVTVSAASTKGPSVFPLAPSSKSTSGGTAALGCLVKDYFPEPVTVSWNSGALTSGVHTFPAVLQSSGLYSLSSVVTVPSSSLGTQTYICNVNHKPSNTKVDKRVEPKS', " +
                            "'QSIGTN', 'YAS', 'QQNNNWPTT', 'GFSLTNYG', 'IWSGGNT', 'ARALTYYDYEFAY'), " +

                            "('EGFRvIII', 'DIELTQSPASLSVATGEKVTIRCMTSTDIDDDMNWYQQKPGEPPKFLISEGNTLRPGVPSRFSSSGTGTDFVFTIENTLSEDVGDYYCLQSFNVPLTFGCGTKLEIK', " +
                            "'QVKLQQSGGGLVKPGASLKLSCVTSGFTFRKFGMSWVRQTSDKCLEWVASISTGGYNTYYSDNVKGRFTISRENAKNTLYLQMSSLKSEDTALYYCTRGYSSTSYAMDYWGQGTTVTVSGIEGR', " +
                            "'', '', '', '', '', '');";

            stmt.execute(insertDataSQL);

            System.out.println("Database initialized and data inserted.");
        } catch (SQLException e) {
            System.out.println("Error while creating protein db: " + e.getMessage());
        }
    }

    public String retrieveSequences(String proteinName) {
        String selectCDRsSQL = "SELECT light_chain, heavy_chain, CDR_L1, CDR_L2, CDR_L3, CDR_H1, CDR_H2, CDR_H3 FROM proteins WHERE protein_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectCDRsSQL)) {

            pstmt.setString(1, proteinName);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder sb = new StringBuilder();

            if (rs.next()) {
                sb.append("Light chain: ").append(rs.getString("light_chain")).append("\n");
                sb.append("Heavy chain: ").append(rs.getString("heavy_chain")).append("\n");
                sb.append("CDR-L1: ").append(rs.getString("CDR_L1")).append("\n");
                sb.append("CDR-L2: ").append(rs.getString("CDR_L2")).append("\n");
                sb.append("CDR-L3: ").append(rs.getString("CDR_L3")).append("\n");
                sb.append("CDR-H1: ").append(rs.getString("CDR_H1")).append("\n");
                sb.append("CDR-H2: ").append(rs.getString("CDR_H2")).append("\n");
                sb.append("CDR-H3: ").append(rs.getString("CDR_H3")).append("\n");
            } else {
                sb.append("No protein found with name: ").append(proteinName);
            }
            return sb.toString();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}