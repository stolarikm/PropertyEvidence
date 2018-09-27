package backend;

import common.DBUtils;
import common.DatabaseFaultException;
import common.IllegalEntityException;
import common.ValidateInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * An implementation of the PropertyManager interface
 * @author Martin Balucha
 */
public class PropertyManagerImpl implements PropertyManager {

    private DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(PropertyManagerImpl.class);

    public PropertyManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void createProperty(Property property) {
        ValidateInput.validateProperty(property);
        if (property.getId() != null) {
            throw new IllegalEntityException("ID is already set");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement
                    ("INSERT INTO Property (area, price, type, address) VALUES (?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);
            statement.setBigDecimal(1, property.getArea());
            statement.setBigDecimal(2, property.getPrice());
            statement.setString(3, property.getType().toString());
            statement.setString(4, property.getAddress());

            statement.executeUpdate();
            Long id = DBUtils.getId(statement.getGeneratedKeys());
            property.setId(id);

            connection.commit();
            connection.setAutoCommit(true);
            log.debug("created property with id " + id);
        } catch(SQLException ex) {
            log.error("can not create property, database fault");
            System.out.println("Error occurred during the inserting new property");
        }

    }


    @Override
    public void deleteProperty(Property property) {
        ValidateInput.validateProperty(property);
        if(property.getId() == null) {
            throw new IllegalEntityException("Property is not in the database");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM Property WHERE id = ?")) {

            statement.setLong(1, property.getId());
            statement.executeUpdate();

            connection.commit();
            log.debug("deleted property with id " + property.getId());
        } catch(SQLException ex) {
            log.error("can not delete property, database fault");
            System.out.println("Error occurred during deleting");
        }
    }


    @Override
    public void updateProperty(Property property) {
        ValidateInput.validateProperty(property);
        if(property.getId() == null) {
            throw new IllegalEntityException("ID cannot be null");
        }
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement
                    ("UPDATE Property SET area = ?, price = ?, type = ?, address = ? WHERE id = ?")) {

            connection.setAutoCommit(false);
            statement.setBigDecimal(1, property.getArea());
            statement.setBigDecimal(2, property.getPrice());
            statement.setString(3, property.getType().toString());
            statement.setString(4, property.getAddress());
            statement.setLong(5, property.getId());
            if (statement.executeUpdate() != 1) {
                connection.setAutoCommit(true);
                throw new IllegalEntityException("Client is not in the database");
            }
            connection.commit();
            connection.setAutoCommit(true);
            log.debug("updated property with id " + property.getId());
        } catch(SQLException ex) {
            log.error("can not update property, database fault");
            System.out.println("Error occurred during update");
        }
    }


    @Override
    public List<Property> getAllProperties() {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("Select * FROM property")) {
                log.debug("retrieving properties");
                return executeQueryForMoreProperties(statement);
        } catch(SQLException ex) {
            log.error("can not retrieve property, database fault");
            System.out.println("Error occurred during retrieving all properties");
            return null;
        }
    }


    @Override
    public List<Property> findPropertyByAddress(String address) {
        if(address == null) {
            throw new IllegalArgumentException("Address is null");
        }
        String addressPattern = "%" + address.toLowerCase() + "%";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM Property WHERE LOWER (address) LIKE ?");) {

            statement.setString(1, addressPattern);
            log.debug("retrieving properties by address " + address);
            return executeQueryForMoreProperties(statement);

        } catch(SQLException ex) {
            log.error("can not retrieve property by address, database fault");
            System.out.println("Error occurred during finding");
            return null;
        }
    }


    @Override
    public List<Property> findPropertyByPrice(BigDecimal price) {
        if(price == null || price.compareTo(new BigDecimal("0")) <= 0) {
            throw new IllegalArgumentException("Invalid price parameter");
        }
        final BigDecimal radius = new BigDecimal("2000");
        BigDecimal lowerBound = price.subtract(radius);
        BigDecimal upperBound = price.add(radius);
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM Property WHERE price >= ? AND price <= ?")) {

                statement.setBigDecimal(1, lowerBound);
                statement.setBigDecimal(2, upperBound);
                log.debug("retrieving properties by price " + price);
                return executeQueryForMoreProperties(statement);

        } catch(SQLException ex) {
            log.error("can not retrieve property by price, database fault");
            System.out.println("Error occurred during finding");
            return null;
        }


    }


    private static List<Property> executeQueryForMoreProperties(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {

            List<Property> resultList = new ArrayList<>();
            while (resultSet.next()) {
                resultList.add(convertDBRowToProperty(resultSet));
            }
            return resultList;
        }
    }


    /**
     * Builds full property from the row in the database table
     * @param resultSet
     * @return  a fully built property object
     * @throws SQLException when getting the value of the attribute from the row fails
     */
    public static Property convertDBRowToProperty(ResultSet resultSet) throws SQLException {
        Property property = new Property();
        property.setAddress(resultSet.getString("address"));
        property.setId(resultSet.getLong("id"));
        property.setType(PropertyType.valueOf(resultSet.getString("type").toUpperCase()));
        property.setArea(resultSet.getBigDecimal("area"));
        property.setPrice(resultSet.getBigDecimal("price"));
        return property;
    }

    @Override
    public Property getPropertyById(Long id) {
        PreparedStatement st;
        try (Connection conn = dataSource.getConnection()) {
            st = conn.prepareStatement(
                    "SELECT * FROM PROPERTY WHERE id = ?");
            st.setLong(1, id);
            return retrieveProperty(st);
        } catch (SQLException ex) {
            throw new DatabaseFaultException("Error occurred while retrieving property from database");
        }
    }

    @Override
    public Property retrieveProperty(PreparedStatement st) throws SQLException {
        try (ResultSet rs = st.executeQuery()) {
            if (rs.next()) {
                return PropertyManagerImpl.convertDBRowToProperty(rs);
            }
            return null;
        }
    }

}
