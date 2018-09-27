package backend;

import common.DBUtils;
import common.DatabaseFaultException;
import common.IllegalEntityException;
import common.ValidateInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ContractManager interface.
 *
 * @author Michal Stolárik 456173@mail.muni.cz, Martin Balucha
 */
public class ContractManagerImpl implements ContractManager {

    private DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(ContractManagerImpl.class);

    public ContractManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract is null");
        }

        if (contract.getId() != null) {
            throw new IllegalEntityException("Contract id is already set");
        }

        ValidateInput.validateContract(contract);

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO CONTRACT (clientid, propertyid, dateofsigning) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, contract.getClient().getId());
            st.setLong(2, contract.getProperty().getId());
            st.setDate(3, DBUtils.toSqlDate(contract.getDateOfSigning()));

            st.executeUpdate();

            Long id = DBUtils.getId(st.getGeneratedKeys());
            contract.setId(id);

            conn.commit();
            conn.setAutoCommit(true);
            log.debug("created contract with id " + id);
        } catch (SQLException ex) {
            log.error("can not create contract, database fault");
            throw new DatabaseFaultException("Error occurred while creating contract in database");
        }
    }

    @Override
    public void deleteContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract is null");
        }

        if (contract.getId() == null) {
            throw new IllegalEntityException("Contract id is null");
        }

        ValidateInput.validateContract(contract);

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM CONTRACT WHERE id=?");
            st.setLong(1, contract.getId());

            if (st.executeUpdate() != 1) {
                conn.setAutoCommit(true);
                throw new IllegalEntityException("Contract is not in the database");
            }

            conn.commit();
            conn.setAutoCommit(true);
            log.debug("deleted contract with id " + contract.getId());
        } catch (SQLException ex) {
            log.error("can not delete contract, database fault");
            throw new DatabaseFaultException("Error occurred while deleting contract from database");
        }
    }

    @Override
    public List<Contract> getAllContracts() {
        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM CONTRACT");
            log.debug("retrieving contracts");
            return retrieveContractsByQuery(st);
        } catch (SQLException ex) {
            log.error("can not retrieve contracts, database fault");
            throw new DatabaseFaultException("Error occurred while retrieving contracts from database");
        }
    }

    @Override
    public void updateContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract is null");
        }

        if (contract.getId() == null) {
            throw new IllegalEntityException("Contract id is null");
        }

        ValidateInput.validateContract(contract);


        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE CONTRACT SET dateofsigning = ? WHERE id=?");
            st.setDate(1, DBUtils.toSqlDate(contract.getDateOfSigning()));
            st.setLong(2, contract.getId());

            if (st.executeUpdate() != 1) {
                conn.setAutoCommit(true);
                throw new IllegalEntityException("Contract is not in the database");
            }

            conn.commit();
            conn.setAutoCommit(true);
            log.debug("updated contract with id " + contract.getId());
        } catch (SQLException ex) {
            log.error("can not update contract, database fault");
            throw new DatabaseFaultException("Error occurred while updating contracts in database");
        }
    }

    @Override
    public List<Contract> findContractsByClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client is null");
        }

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM CONTRACT WHERE clientid = ?");
            st.setLong(1, client.getId());
            log.debug("retrieving contracts with by client with id " + client.getId());
            return retrieveContractsByQuery(st);
        } catch (SQLException ex) {
            log.error("cannot retrieve contracts by client, database fault");
            throw new DatabaseFaultException("Error occurred while retrieving contracts from database");
        }
    }

    @Override
    public List<Contract> findContractByProperty(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Property is null");
        }

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM CONTRACT WHERE propertyid = ?");
            st.setLong(1, property.getId());
            log.debug("retrieving contracts with by property with id " + property.getId());
            return retrieveContractsByQuery(st);
        } catch (SQLException ex) {
            log.error("can not retrieve contracts by property, database fault");
            throw new DatabaseFaultException("Error occurred while retrieving contracts from database");
        }
    }

    @Override
    public Contract getContractById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(
                    "SELECT * FROM CONTRACT WHERE id = ?");
            st.setLong(1, id);
            return retrieveContract(st);
        } catch (SQLException ex) {
            throw new DatabaseFaultException("Error occurred while retrieving contract from database");
        }
    }



    public Contract retrieveContract(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return rowToContract(rs);
            }
            return null;
        }
    }



    /**
     * Retrieves list of contracts from database, by SQL query statement
     *
     * @param st prepared SQL statement, to retrieve contracts by
     * @return list of contracts, retrieved by statement
     * @throws SQLException when error occurs while retrieving from database
     */
    private List<Contract> retrieveContractsByQuery(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Contract> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToContract(rs));
        }
        return result;
    }

    /**
     * Converts database row to Contract object
     *
     * @param rs result set containing a row from database
     * @return converted Contract object
     * @throws SQLException when error occurs while converting
     */
    private Contract rowToContract(ResultSet rs) throws SQLException {
        ClientManager clientManager = new ClientManagerImpl(dataSource);
        PropertyManager propertyManager = new PropertyManagerImpl(dataSource);

        Contract result = new Contract();
        Long id = rs.getLong("id");
        result.setId(id);
        Long clientId = (rs.getLong("clientid"));
        result.setClient(clientManager.getClientById(clientId));
        Long propertyId = (rs.getLong("propertyid"));
        result.setProperty(propertyManager.getPropertyById(propertyId));
        result.setDateOfSigning(DBUtils.toLocalDate(rs.getDate("dateofsigning")));
        return result;
    }
}