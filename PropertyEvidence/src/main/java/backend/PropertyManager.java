package backend;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * An interface declaring CRUD operations of the PropertyManager
 * @author Martin Balucha
 */
public interface PropertyManager {

    /**
     * Adds new property to the evidence.
     * @param property  a property which is to be added to the evidence
     * @throws IllegalArgumentException when the property is null
     */
    void createProperty(Property property);


    /**
     * Deletes property from the evidence
     * @param property  a property which is to be deleted
     * @throws common.IllegalEntityException when the property is not in the database
     * @throws IllegalArgumentException when the property parameter is null
     */
    void deleteProperty(Property property);


    /**
     * Updates an existing property
     * @param property a property which is to be updated
     * @throws IllegalArgumentException if the property parameter is null
     * @throws common.IllegalEntityException if the entity does not exist in the database
     */
    void updateProperty(Property property);


    /**
     * Gets a list of all properties in the evidence
     * @return  a list of all properties
     * @throws SQLException
     */
    List<Property> getAllProperties();


    /**
     * Finds all properties with given address or city
     * @param address    an address of the wanted properties
     * @return          a list of all properties on the wanted address
     * @throws IllegalArgumentException if the address is null, is equal to 0 or negative
     */
    List<Property> findPropertyByAddress(String address);


    /**
     * Finds all properties with the same or similar price
     * @param price a desired price of the property
     * @return      a list of all properties with the same price
     * @throws IllegalArgumentException if the price is null, is equal to 0 or negative
     */
    List<Property> findPropertyByPrice(BigDecimal price);

    /**
     * Retrieves property from database by its id
     *
     * @param id primary key
     * @return retrieved property
     */
    Property getPropertyById(Long id);

    /**
     * Help method for getPropertyById
     *
     * @param st prepared SQL statement
     * @return single Property object, or null
     * @throws SQLException when error occurs while retrieving property
     */
    Property retrieveProperty(PreparedStatement st) throws SQLException;


}
