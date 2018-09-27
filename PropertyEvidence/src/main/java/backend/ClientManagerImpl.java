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
 * Implementation of ClientManager.
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ClientManagerImpl implements ClientManager {

    private DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(ClientManagerImpl.class);

    public ClientManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createClient(Client client) {
        ValidateInput.validateClient(client);

        if (client.getId() != null) {
            throw new IllegalEntityException("Clients id is already set");
        }

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st;
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO CLIENT (fullname, phone) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, client.getFullName());
            st.setString(2, client.getPhoneNumber());

            st.executeUpdate();

            Long id = DBUtils.getId(st.getGeneratedKeys());
            client.setId(id);

            conn.commit();
            conn.setAutoCommit(true);
            log.debug("client created with id " + id);
        } catch (SQLException ex) {
            log.error("can not create client, database fault");
            throw new DatabaseFaultException("Error occurred while adding client to database");
        }
    }

    @Override
    public void updateClient(Client client) {
        ValidateInput.validateClient(client);

        if (client.getId() == null) {
            throw new IllegalEntityException("Clients id is null");
        }

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE CLIENT SET fullname = ?, phone = ? WHERE id = ?");
            st.setString(1, client.getFullName());
            st.setString(2, client.getPhoneNumber());
            st.setLong(3, client.getId());

            if (st.executeUpdate() != 1) {
                conn.setAutoCommit(true);
                throw new IllegalEntityException("Client is not in the database");
            }

            conn.commit();
            conn.setAutoCommit(true);
            log.debug("client with id " + client.getId() + " updated");
        } catch (SQLException ex) {
            log.error("can not update client, database fault");
            throw new DatabaseFaultException("Error occurred while updating client in database");
        }
    }

    @Override
    public void deleteClient(Client client) {
        ValidateInput.validateClient(client);

        if (client.getId() == null) {
            throw new IllegalEntityException("Clients id is null");
        }

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM CLIENT WHERE id = ?");
            st.setLong(1, client.getId());

            if (st.executeUpdate() != 1) {
                conn.setAutoCommit(true);
                throw new IllegalEntityException("Client is not in the database");
            }

            conn.commit();
            conn.setAutoCommit(true);
            log.debug("client with id " + client.getId() + " deleted");
        } catch (SQLException ex) {
            log.error("can not delete client, database fault");
            throw new DatabaseFaultException("Error occurred while deleting client from database");
        }
    }

    @Override
    public List<Client> getClients() {
        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM CLIENT");
            log.debug("retrieving clients");
            return retrieveClientsByQuery(st);
        } catch (SQLException ex) {
            log.error("can not retrieve client, database fault");
            throw new DatabaseFaultException("Error occurred while retrieving clients from database");
        }
    }

    @Override
    public List<Client> findClientsByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Provided name is null");
        }

        String patternToFind = "%" + name.toLowerCase() + "%";

        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM CLIENT WHERE LOWER(fullname) LIKE ?");
            st.setString(1, patternToFind);
            log.debug("retrieving clients by name " + name);
            return retrieveClientsByQuery(st);
        } catch (SQLException ex) {
            log.error("can not retrieve by name, database fault");
            throw new DatabaseFaultException("Error occurred while retrieving clients from database");
        }
    }

    /**
     * Retrieves list of clients from database, by SQL query statement
     *
     * @param st prepared SQL statement, to retrieve clients by
     * @return list of clients, retrieved by statement
     * @throws SQLException when error occurs while retrieving from database
     */
    private static List<Client> retrieveClientsByQuery(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Client> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rowToClient(rs));
        }
        return result;
    }

    /**
     * Converts database row to Client object
     *
     * @param rs result set containing a row from database
     * @return converted Client object
     * @throws SQLException when error occurs while converting
     */
    public static Client rowToClient(ResultSet rs) throws SQLException {
        Client result = new Client();
        result.setId(rs.getLong("id"));
        result.setFullName(rs.getString("fullname"));
        result.setPhoneNumber(rs.getString("phone"));
        return result;
    }

    /**
     * Retrieves client from database by its id
     *
     * @param id primary key
     * @return retrieved client
     */
    public Client getClientById(Long id) {
        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM CLIENT WHERE id = ?");
            st.setLong(1, id);
            return retrieveClient(st);
        } catch (SQLException ex) {
            throw new DatabaseFaultException("Error occurred while retrieving client from database");
        }
    }


    /**
     * Help method for getClientById
     *
     * @param st prepared SQL statement
     * @return single Client object, or null
     * @throws SQLException when error occurs while retrieving client
     */
    public Client retrieveClient(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return ClientManagerImpl.rowToClient(rs);
            }
            return null;
        }
    }
}