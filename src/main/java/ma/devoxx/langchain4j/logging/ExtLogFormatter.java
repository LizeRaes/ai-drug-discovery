package ma.devoxx.langchain4j.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.Date;

// Custom Formatter
public class ExtLogFormatter extends Formatter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

        // Date and Time
        sb.append(dateFormat.format(new Date(record.getMillis()))).append(" ");

        // Log Level
        sb.append("[").append(record.getLevel()).append("] ");

        // Log Message
        sb.append(record.getMessage());

        return sb.toString();
    }
}
