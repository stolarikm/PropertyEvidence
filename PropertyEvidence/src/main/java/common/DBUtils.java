package common;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DBUtils {

    /**
     * Executes SQL script from a file
     *
     * @param ds        database data source
     * @param scriptUrl path to file with script
     * @throws SQLException whe error occurs executing script
     */
    public static void executeSqlScript(DataSource ds, URL scriptUrl) throws SQLException {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            for (String sqlStatement : readSqlStatements(scriptUrl)) {
                if (!sqlStatement.trim().isEmpty()) {
                    conn.prepareStatement(sqlStatement).executeUpdate();
                }
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * Reads SQL statement from SQL script
     *
     * @param url path to a file with script
     * @return String containing SQL statement
     */
    private static String[] readSqlStatements(URL url) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read " + url, ex);
        }
    }


    /**
     * Get id of entity stored in database
     *
     * @param key result set to find id in
     * @return id if there is one
     * @throws SQLException when error occurs, for example there is no column named id
     */
    public static Long getId(ResultSet key) throws SQLException {
        if (key.getMetaData().getColumnCount() != 1) {
            throw new IllegalArgumentException("Too many columns in result set");
        }
        if (key.next()) {
            Long result = key.getLong(1);
            if (key.next()) {
                throw new IllegalArgumentException("Too many rows in result set");
            }
            return result;
        } else {
            throw new IllegalArgumentException("Id is missing");
        }
    }

    /**
     * Converts LocalDate to SQLDate
     *
     * @param localDate date to convert
     * @return converted date
     */
    public static Date toSqlDate(LocalDate localDate) {
        if (localDate != null) {
            return Date.valueOf(localDate);
        }
        return null;
    }

    /**
     * Converts SQLDate to LocalDate
     *
     * @param date date to convert
     * @return converted date
     */
    public static LocalDate toLocalDate(Date date) {
        if (date != null) {
            return date.toLocalDate();
        }
        return null;
    }
}
