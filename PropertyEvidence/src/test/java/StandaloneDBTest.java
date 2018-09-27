import backend.*;
import common.DBUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for class BuilderManagerImpl
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class StandaloneDBTest {

    private ClientManager manager;
    private DataSource dataSource;


    /**
     * Database called PropertyEvidence need to be running on localhost port 1527 before running the tests.
     * URL: jdbc:derby://localhost:1527/PropertyEvidence
     *
     * @throws IOException when error occurs reading database configuration properties
     * @throws SQLException when error occurs during connecting to database
     */
    public StandaloneDBTest() throws SQLException {
        dataSource = Main.connectToStandaloneDatabase();
        DBUtils.executeSqlScript(dataSource, ClientManager.class.getClassLoader().getResource("dropClientTable.sql"));
    }

    @Before
    public void setUp() throws SQLException {
        DBUtils.executeSqlScript(dataSource, ClientManager.class.getClassLoader().getResource("createClientTable.sql"));
        manager = new ClientManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, ClientManager.class.getClassLoader().getResource("dropClientTable.sql"));
    }

    /**
     * Returns sample valid ClientBuilder, without id set.
     *
     * @return ClientBuilder object
     */
    private ClientBuilder sampleClientBuilder() {
        return new ClientBuilder()
                .fullName("Name Surname")
                .phoneNumber("+420915111999");
    }

    @Test
    public void createAndRetrieveClient() {
        Client clientToRetrieve = sampleClientBuilder().buildClient();
        manager.createClient(clientToRetrieve);
        List<Client> result = manager.getClients();
        assertThat(result)
                .containsOnly(clientToRetrieve);
    }
}