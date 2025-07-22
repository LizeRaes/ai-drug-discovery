package ma.devoxx.langchain4j.dbs;

import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.model.chat.ChatModel;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SequenceDbContentRetriever {

    public SqlDatabaseContentRetriever get(ChatModel chatLanguageModel) {
        DataSource dataSource = createDataSource();

        return SqlDatabaseContentRetriever.builder()
                .dataSource(dataSource)
                .chatModel(chatLanguageModel)
                .build();
    }

    private static DataSource createDataSource() {

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:src/main/resources/dbs/protein_structure.db");

        //String createTablesScript = read("sql/create_tables.sql");
        //execute(createTablesScript, dataSource);

        //String prefillTablesScript = read("sql/prefill_tables.sql");
        //execute(prefillTablesScript, dataSource);

        return dataSource;
    }

    private static String read(String path) {
        try {
            return new String(Files.readAllBytes(toPath(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void execute(String sql, DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            for (String sqlStatement : sql.split(";")) {
                statement.execute(sqlStatement.trim());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = SequenceDbContentRetriever.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}