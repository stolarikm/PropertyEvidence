import backend.Client;
import backend.ClientManager;
import backend.ClientManagerImpl;
import common.DBUtils;
import common.IllegalEntityException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for class BuilderManagerImpl
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ClientManagerImplTest {

    private ClientManager manager;
    private DataSource dataSource;

    private static DataSource getDataSource() {
        EmbeddedDataSource source = new EmbeddedDataSource();
        source.setDatabaseName("memory:PropertyEvidenceDB");
        source.setCreateDatabase("create");
        return source;
    }

    @Before
    public void setUp() throws SQLException {
        dataSource = getDataSource();
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

    /* -------------------------------------------------------------------------
     * CreateClient tests
     * -------------------------------------------------------------------------
     */
    @Test
    public void createClientValid() {
        manager.createClient(sampleClientBuilder().buildClient());

        Client client = sampleClientBuilder().buildClient();
        manager.createClient(client);

        Long clientId = client.getId();
        assertThat(clientId).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createClientNull() {
        manager.createClient(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void createClientWithIdSet() {
        manager.createClient(sampleClientBuilder().id(1L).buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void createClientInvalidFullNameWithoutSurname() {
        manager.createClient(sampleClientBuilder().fullName("Name").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void createClientInvalidFullNameWithNumber() {
        manager.createClient(sampleClientBuilder().fullName("Name Surn4me").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void createClientInvalidPhoneNumber() {
        manager.createClient(sampleClientBuilder().phoneNumber("+421905k25984").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void createClientEmptyPhoneNumber() {
        manager.createClient(sampleClientBuilder().phoneNumber("").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void createClientEmptyFullName() {
        manager.createClient(sampleClientBuilder().fullName("").buildClient());
    }

    /* -------------------------------------------------------------------------
     * Update client tests
     * -------------------------------------------------------------------------
     */
    @Test(expected = IllegalArgumentException.class)
    public void updateClientNull() {
        manager.updateClient(null);
    }

    @Test
    public void updateClientValid() {
        Client clientForUpdate = sampleClientBuilder().buildClient();
        manager.createClient(clientForUpdate);
        clientForUpdate.setFullName("New name");
        manager.updateClient(clientForUpdate);
        assertThat(manager.getClients()).containsOnly(clientForUpdate);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateClientWithoutId() {
        manager.updateClient(sampleClientBuilder().buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void updateClientInvalidFullNameWithoutSurname() {
        manager.updateClient(sampleClientBuilder().id(5L).fullName("Name").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void updateClientInvalidFullNameWithNumber() {
        manager.updateClient(sampleClientBuilder().id(6L).fullName("Name Surnam3").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void updateClientInvalidPhoneNumber() {
        manager.updateClient(sampleClientBuilder().id(0L).phoneNumber("+421905k26984").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void updateClientEmptyPhoneNumber() {
        manager.updateClient(sampleClientBuilder().id(14L).phoneNumber("").buildClient());
    }

    @Test(expected = IllegalEntityException.class)
    public void updateClientEmptyFullName() {
        manager.updateClient(sampleClientBuilder().id(46L).fullName("").buildClient());
    }

    /* -------------------------------------------------------------------------
     * Delete client tests
     * -------------------------------------------------------------------------
     */
    @Test
    public void deleteClientValid() {
        Client clientForDelete = sampleClientBuilder().buildClient();
        manager.createClient(clientForDelete);

        manager.deleteClient(clientForDelete);
        assertThat(manager.getClients()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteClientNull() {
        manager.deleteClient(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteClientWithoutId() {
        manager.deleteClient(sampleClientBuilder().buildClient());
    }

    /* -------------------------------------------------------------------------
     * GetClients tests
     * -------------------------------------------------------------------------
     */
    @Test
    public void getClientsEmptyDB() {
        List<Client> result = manager.getClients();
        assertThat(result).isEmpty();
    }

    @Test
    public void getClientsOnlyOneInDB() {
        Client clientToRetrieve = sampleClientBuilder().buildClient();
        manager.createClient(clientToRetrieve);
        List<Client> result = manager.getClients();
        assertThat(result)
                .containsOnly(clientToRetrieve);
    }

    /* -------------------------------------------------------------------------
     * FindClientsByName tests
     * -------------------------------------------------------------------------
     */
    @Test(expected = IllegalArgumentException.class)
    public void findClientsByNameNull() {
        manager.findClientsByName(null);
    }

    @Test
    public void findClientsByNameNotInDB() {
        manager.findClientsByName("Notin Database");
        List<Client> result = manager.getClients();
        assertThat(result).isEmpty();
    }

    @Test
    public void findClientsByNameSingleClientByFullName() {
        Client clientToFind = sampleClientBuilder().buildClient();
        String nameToFind = clientToFind.getFullName();
        manager.createClient(clientToFind);
        List<Client> result = manager.findClientsByName(nameToFind);
        assertThat(result)
                .containsOnly(clientToFind);
    }

    @Test
    public void findClientsByNameSingleClientBySubName() {
        Client clientToFind = sampleClientBuilder().fullName("Jack Daniels").buildClient();
        String nameToFind = "Jack";
        manager.createClient(clientToFind);
        List<Client> result = manager.findClientsByName(nameToFind);
        assertThat(result)
                .containsOnly(clientToFind);
    }
}