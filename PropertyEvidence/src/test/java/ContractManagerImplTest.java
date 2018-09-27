import backend.*;
import common.DBUtils;
import common.IllegalEntityException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Test class for class ContractManagerImpl
 *
 * @author Michal Stolárik 456173@mail.muni.cz, Martin Balucha
 */
public class ContractManagerImplTest {
    private ContractManager manager;
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
        DBUtils.executeSqlScript(dataSource, ContractManager.class.getClassLoader().getResource("createContractTable.sql"));
        DBUtils.executeSqlScript(dataSource, ContractManager.class.getClassLoader().getResource("createClientTable.sql"));
        DBUtils.executeSqlScript(dataSource, ContractManager.class.getClassLoader().getResource("createPropertyTable.sql"));
        manager = new ContractManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource, ContractManager.class.getClassLoader().getResource("dropContractTable.sql"));
        DBUtils.executeSqlScript(dataSource, ContractManager.class.getClassLoader().getResource("dropClientTable.sql"));
        DBUtils.executeSqlScript(dataSource, ContractManager.class.getClassLoader().getResource("dropPropertyTable.sql"));
    }


    private ContractBuilder testingContractBuilder() {
        Client client = new ClientBuilder()
                .fullName("John Doe")
                .id(null)
                .phoneNumber("+1855224674")
                .buildClient();

        ClientManager clientManager = new ClientManagerImpl(dataSource);
        clientManager.createClient(client);

        Property property = new PropertyBuilder()
                .price(new BigDecimal("70.00"))
                .area(new BigDecimal("500000.00"))
                .id(null)
                .address("Hybe")
                .type(PropertyType.ONE_ROOM_FLAT)
                .buildProperty();

        PropertyManager propertyManager = new PropertyManagerImpl(dataSource);
        propertyManager.createProperty(property);

        return new ContractBuilder()
                .client(client)
                .dateOfSigning(LocalDate.parse("2018-01-01"))
                .property(property);
    }


    private ContractBuilder anotherTestingContractBuilder() {
        Client client = new ClientBuilder()
                .fullName("John Milton")
                .id(null)
                .phoneNumber("+421944000000")
                .buildClient();

        ClientManager clientManager = new ClientManagerImpl(dataSource);
        clientManager.createClient(client);

        Property property = new PropertyBuilder()
                .price(new BigDecimal("152.00"))
                .area(new BigDecimal("1578000.00"))
                .id(null)
                .address("Ludovit")
                .type(PropertyType.THREE_ROOM_FLAT)
                .buildProperty();

        PropertyManager propertyManager = new PropertyManagerImpl(dataSource);
        propertyManager.createProperty(property);

        return new ContractBuilder()
                .property(property)
                .client(client)
                .dateOfSigning(LocalDate.parse("2013-12-31"));
    }


    /* Create operation tests */


    @Test(expected = IllegalArgumentException.class)
    public void createNullContract() {
        manager.createContract(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void createContractWithNullProperty() {
        Contract contract = testingContractBuilder().property(null).buildContract();
        manager.createContract(contract);
    }


    @Test(expected = IllegalEntityException.class)
    public void createContractWithNullClient() {
        Contract contract = testingContractBuilder().client(null).buildContract();
        manager.createContract(contract);
    }


    @Test(expected = IllegalEntityException.class)
    public void createContractWithInvalidDate() {
        Contract contract = testingContractBuilder().dateOfSigning(LocalDate.parse("9999-12-31")).buildContract();
        manager.createContract(contract);
    }


    @Test
    public void createValidContract() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);

        List<Contract> contractList = manager.getAllContracts();
        assertThat(contractList.contains(contract));
    }


    @Test
    public void createValidContractToEmptyDatabase() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);

        List<Contract> contractList = manager.getAllContracts();
        assertThat(contractList).containsOnly(contract);
    }

    @Test
    public void createValidContractToNonEmptyDatabase() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);

        Contract anotherContract = anotherTestingContractBuilder().buildContract();
        manager.createContract(anotherContract);

        List<Contract> contractList = manager.getAllContracts();
        assertThat(contractList).contains(contract, anotherContract);
    }


    /* Find operations tests */


    @Test(expected = IllegalArgumentException.class)
    public void findByNullProperty() {
        manager.findContractByProperty(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void findByNullClient() {
        manager.findContractsByClient(null);
    }


    @Test
    public void findByPropertyValidTest() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);

        Contract anotherContract = testingContractBuilder().buildContract();
        manager.createContract(anotherContract);

        List<Contract> found = manager.findContractByProperty(contract.getProperty());

        assertThat(found.contains(contract));
    }


    @Test
    public void findByClientValidTest() {
        Contract contract = anotherTestingContractBuilder().buildContract();
        manager.createContract(contract);

        List<Contract> foundList = manager.findContractsByClient(contract.getClient());

        assertThat(foundList).containsOnly(contract);
    }


    @Test
    public void findByClientMultipleContracts() {
        Contract first = testingContractBuilder().buildContract();
        Contract second = anotherTestingContractBuilder().client(first.getClient()).buildContract();
        Contract third = anotherTestingContractBuilder().buildContract();

        manager.createContract(first);
        manager.createContract(second);
        manager.createContract(third);

        List<Contract> foundList = manager.findContractsByClient(first.getClient());

        assertThat(foundList).containsOnly(first, second);
    }


    @Test
    public void getAllContractsEmptyDatabase() {
        List<Contract> contracts = manager.getAllContracts();
        assertThat(contracts).isEmpty();
    }



    /* Delete operation tests */


    @Test(expected = IllegalArgumentException.class)
    public void deleteNullContract() {
        manager.deleteContract(null);
    }


    @Test(expected = IllegalEntityException.class)
    public void deleteNonExistingContract() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);

        Contract notExisting = anotherTestingContractBuilder().buildContract();
        manager.deleteContract(notExisting);
    }

    @Test
    public void deleteExistingContract() {
        Contract contract = anotherTestingContractBuilder().buildContract();
        manager.createContract(contract);

        manager.deleteContract(contract);
        List<Contract> allContracts = manager.getAllContracts();
        assertThat(allContracts).isEmpty();
    }


    @Test
    public void deleteWhenMoreContractsArePresent() {
        Contract first = testingContractBuilder().buildContract();
        Contract second = testingContractBuilder().buildContract();
        manager.createContract(first);
        manager.createContract(second);

        manager.deleteContract(first);
        List<Contract> properties = manager.getAllContracts();

        assertThat(properties).containsOnly(second);
    }


    /* Update operation tests */


    @Test(expected = IllegalArgumentException.class)
    public void updateNullContract() {
        manager.updateContract(null);
    }


    @Test(expected = IllegalEntityException.class)
    public void updateContractWithNullClient() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);
        contract.setClient(null);
        manager.updateContract(contract);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateContractNullProperty() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);
        contract.setProperty(null);
        manager.updateContract(contract);
    }


    @Test(expected = IllegalEntityException.class)
    public void updateContractWithNullDate() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);
        contract.setDateOfSigning(null);
        manager.updateContract(contract);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyInvalidDate() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);
        contract.setDateOfSigning(LocalDate.parse("9999-12-31"));
        manager.updateContract(contract);
    }


    @Test
    public void updateValidDate() {
        Contract contract = testingContractBuilder().buildContract();
        Contract antherContract = anotherTestingContractBuilder().buildContract();
        manager.createContract(contract);
        manager.createContract(antherContract);

        contract.setDateOfSigning(LocalDate.parse("1996-12-31"));
        manager.updateContract(contract);

        List<Contract> contracts = manager.getAllContracts();
        assertThat(contracts).containsOnly(contract, antherContract);
    }


    @Test
    public void updatePropertyEmptyDatabase() {
        Contract contract = testingContractBuilder().buildContract();
        manager.createContract(contract);

        contract.setDateOfSigning(LocalDate.parse("1999-11-17"));
        manager.updateContract(contract);

        List<Contract> contracts = manager.getAllContracts();
        assertThat(contracts.contains(contract));
    }

}