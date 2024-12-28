package ma.devoxx.langchain4j.dbs;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to save the history of the conversation.
 */
public class HistoryDatabase {

    private static final String DATABASE_PATH = "src/main/resources/dbs/conversation_history.db";
    private static final String DB_URL = "jdbc:sqlite:" + DATABASE_PATH;

    public static void main(String[] args) {
        HistoryDatabase db = new HistoryDatabase();
        //db.createDatabase();
        db.cleanup();
        db.insertMessages(new UserMessage("What's your name ?"), new AiMessage("GPT, and you ?"));
        System.out.println(db.retrieveConversation());
    }

    public void cleanup() {
        String deleteHistory = "DELETE FROM history";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteHistory);
        } catch (SQLException e) {
            System.out.println("Error while creating protein db: " + e.getMessage());
        }
    }

    public void createDatabase() {
        String createTableSQL =
                "CREATE TABLE IF NOT EXISTS history ( " +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "user_message TEXT, " +
                        "ai_message TEXT" +
                        ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database initialized and data inserted.");
        } catch (SQLException e) {
            System.out.println("Error while creating history db: " + e.getMessage());
        }
    }

    public void insertMessages(UserMessage userMessage, AiMessage aiMessage) {
        String insertDataSQL = "INSERT INTO history (user_message, ai_message) VALUES (?, ?);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(insertDataSQL)) {
            // Insert protein data

            stmt.setString(1, userMessage.singleText());
            stmt.setString(2, aiMessage.text());
            stmt.execute();

            System.out.println("Database initialized and data inserted.");
        } catch (SQLException e) {
            System.out.println("Error while creating protein db: " + e.getMessage());
        }
    }

    public List<ChatMessage> retrieveConversation() {
        String selectSQL = "SELECT * FROM history";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement pstmt = conn.createStatement()) {

            ResultSet rs = pstmt.executeQuery(selectSQL);

            StringBuilder sb = new StringBuilder();

            List<ChatMessage> messages = new LinkedList<>();
            if (rs.next()) {
                messages.add(new UserMessage(rs.getString("user_message")));
                messages.add(new AiMessage(rs.getString("ai_message")));
            }
            return  messages;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}