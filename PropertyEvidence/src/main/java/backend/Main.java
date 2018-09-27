package backend;

import common.DBUtils;
import frontend.MainForm;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Main {

    /**
     * Creates in-memory database, and connects to it
     *
     * @return DataSource connection to database
     */
    public static DataSource createMemoryDatabase() {
        EmbeddedDataSource source = new EmbeddedDataSource();
        source.setDatabaseName("memory:PropertyEvidenceDB");
        source.setCreateDatabase("create");

        try {
            DBUtils.executeSqlScript(source, ClientManager.class.getClassLoader().getResource("createClientTable.sql"));
            DBUtils.executeSqlScript(source, PropertyManager.class.getClassLoader().getResource("createPropertyTable.sql"));
            DBUtils.executeSqlScript(source, ContractManager.class.getClassLoader().getResource("createContractTable.sql"));
        } catch (SQLException ex) {
            System.out.println("Error occurred during database initialization");
        }
        return source;
    }

    /**
     * Connects to standalone Derby database, specified in dbconfig.properties
     *
     * @return DataSource connection to database
     */
    public static DataSource connectToStandaloneDatabase() {
        try {
            Properties properties = DBproperties();
            BasicDataSource source = new BasicDataSource();
            source.setDriverClassName(properties.getProperty("jdbc.driver"));
            source.setUrl(properties.getProperty("jdbc.url"));
            source.setUsername(properties.getProperty("jdbc.user"));
            source.setPassword(properties.getProperty("jdbc.password"));

            DatabaseMetaData meta = source.getConnection().getMetaData();

            ResultSet result = meta.getTables(null, null, "CLIENT", null);
            if (!result.next()){
                DBUtils.executeSqlScript(source, ClientManager.class.getClassLoader().getResource("createClientTable.sql"));
            }

            result = meta.getTables(null, null, "PROPERTY", null);
            if (!result.next()) {
                DBUtils.executeSqlScript(source, PropertyManager.class.getClassLoader().getResource("createPropertyTable.sql"));
            }

            result = meta.getTables(null, null, "CONTRACT", null);
            if (!result.next()) {
                DBUtils.executeSqlScript(source, ContractManager.class.getClassLoader().getResource("createContractTable.sql"));
            }
            return source;
        } catch (SQLException ex) {
            System.out.println("Error occurred during database initialization: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error occurred during loading configuration properties");
        }
        return null;
    }

    /**
     * Reads properties from dbconfig.properties
     *
     * @return retrieved Properties
     * @throws IOException when error occurs during reading file
     */
    private static Properties DBproperties() throws IOException {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("dbconfig.properties");
        Properties properties = new Properties();
        if (inputStream == null) {
            throw new FileNotFoundException("Database property file not found in the classpath");
        }
        properties.load(inputStream);
        return properties;
    }


    public static void main(String[] args) {
        ResourceBundle messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", Locale.getDefault());

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame(messages.getString("appTitle"));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new MainForm().getPanel());
            frame.setPreferredSize(new Dimension(1000, 600));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
