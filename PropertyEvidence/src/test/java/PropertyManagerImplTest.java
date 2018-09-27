import backend.Property;
import backend.PropertyManager;
import backend.PropertyManagerImpl;
import backend.PropertyType;
import common.IllegalEntityException;

import java.math.BigDecimal;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import javax.sql.DataSource;
import common.DBUtils;
import java.sql.SQLException;
import org.apache.derby.jdbc.EmbeddedDataSource;

import static org.assertj.core.api.Assertions.*;




/**
 * A testing class for PropertyManagerImpl
 * @author Martin Balucha
 */
public class PropertyManagerImplTest {
    private PropertyManager manager;
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        dataSource = getDataSource();
        DBUtils.executeSqlScript(dataSource, PropertyManager.class.getClassLoader().getResource("createPropertyTable.sql"));
        manager = new PropertyManagerImpl(dataSource);
    }


    @After
    public void tearDown() throws SQLException, IOException {
        DBUtils.executeSqlScript(dataSource, PropertyManager.class.getClassLoader().getResource("dropPropertyTable.sql"));
    }


    private static DataSource getDataSource() throws SQLException {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("memory:PropertyEvidenceDatabase");
        dataSource.setCreateDatabase("create");
        return dataSource;
    }


    private PropertyBuilder testingPropertyBuilder() {
        return new PropertyBuilder()
                .id(null)
                .address("Leluchov")
                .area(new BigDecimal("165.00"))
                .price(new BigDecimal("150000.00"))
                .type(PropertyType.HUT);
    }


    private PropertyBuilder anotherTestingPropertyBuilder() {
        return new PropertyBuilder().id(null)
                .address("Giorgij Abazdiev 6, Skopje")
                .area(new BigDecimal("75.00"))
                .price(new BigDecimal("1572000.00"))
                .type(PropertyType.TWO_ROOM_FLAT);
    }


    /* Create operation tests */

    @Test
    public void createPropertyIdIsNotNullTest() {
        Property property = testingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        Long propertyID = property.getId();
        assertThat(propertyID).isNotNull();
    }


    @Test
    public void createValidPropertyEmptyDatabase() {
        Property property = testingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        List<Property> properties = manager.getAllProperties();
        assertThat(properties).containsOnly(property);
    }


    @Test
    public void createValidProperties() {
        Property property = testingPropertyBuilder().buildProperty();
        Property anotherProperty = anotherTestingPropertyBuilder().buildProperty();

        manager.createProperty(property);
        manager.createProperty(anotherProperty);

        List<Property> properties = manager.getAllProperties();
        assertThat(properties).containsOnly(property, anotherProperty);
    }


    @Test
    public void createValidPropertyNonEmptyDatabase() {
        Property property = testingPropertyBuilder().buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalArgumentException.class)
    public void createPropertyAsNull() {
        manager.createProperty(null);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithExistingID() {
        Property property = testingPropertyBuilder().id(1L).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithEmptyAddress() {
        Property property = testingPropertyBuilder().address("").buildProperty();
        manager.createProperty(property);
    }



    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithNullAddres() {
        Property property = testingPropertyBuilder().address(null).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithNegativeArea() {
        Property property = testingPropertyBuilder().area(new BigDecimal("-1")).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithZeroArea() {
        Property property = testingPropertyBuilder().area(new BigDecimal("0")).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithNullArea() {
        Property property = testingPropertyBuilder().area(null).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithNegativePrice() {
        Property property = testingPropertyBuilder().price(new BigDecimal("-1")).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithNullPrice() {
        Property property = testingPropertyBuilder().price(null).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithZeroPrice() {
        Property property = testingPropertyBuilder().price(new BigDecimal("0")).buildProperty();
        manager.createProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void createPropertyWithNullType() {
        Property property = testingPropertyBuilder().type(null).buildProperty();
        manager.createProperty(property);
    }



    /* Delete operation tests*/



    @Test(expected = IllegalArgumentException.class)
    public void deleteNullProperty() {
        manager.deleteProperty(null);
    }


    @Test(expected = IllegalEntityException.class)
    public void deleteNonExistingProperty() {
        Property property = anotherTestingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        Property notExisting = testingPropertyBuilder().buildProperty();
        manager.deleteProperty(notExisting);
    }


    @Test
    public void deleteExistingProperty() {
        Property property = anotherTestingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        manager.deleteProperty(property);
        List<Property> allProperties = manager.getAllProperties();
        assertThat(allProperties).isEmpty();
    }


    @Test
    public void deleteWhenMorePropertiesArePresent() {
        Property first = testingPropertyBuilder().buildProperty();
        Property second = testingPropertyBuilder().buildProperty();
        manager.createProperty(first);
        manager.createProperty(second);

        manager.deleteProperty(first);
        List<Property> properties = manager.getAllProperties();

        assertThat(properties).containsOnly(second);
    }


    /* Find operations tests */


    @Test(expected = IllegalArgumentException.class)
    public void findNullAddressProperty() {
        manager.findPropertyByAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findEmptyAddressProperty() {
        manager.findPropertyByAddress("");
    }


    @Test(expected = IllegalArgumentException.class)
    public void findNullPriceProperty() {
        manager.findPropertyByPrice(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void findNegativePriceProperty() {
        manager.findPropertyByPrice(new BigDecimal("-1"));
    }



    @Test
    public void findByAddressExisting() {
        Property property = anotherTestingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        List<Property> foundList = manager.findPropertyByAddress(property.getAddress());
        assertThat(foundList.contains(property));
    }


    @Test
    public void findByAddressMultipleProperties() {
        Property first = testingPropertyBuilder().buildProperty();
        Property second = anotherTestingPropertyBuilder().address(first.getAddress()).buildProperty();
        Property third = anotherTestingPropertyBuilder().buildProperty();

        manager.createProperty(first);
        manager.createProperty(second);
        manager.createProperty(third);

        List<Property> found = manager.findPropertyByAddress(first.getAddress());
        assertThat(found).containsOnly(first, second);
    }


    @Test
    public void findByPriceExisting() {
        Property property = testingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        List<Property> foundList = manager.findPropertyByPrice(property.getPrice());
        assertThat(foundList.contains(property));
    }


    @Test
    public void findByPriceNonExisting() {
        Property property = anotherTestingPropertyBuilder().buildProperty();
        manager.createProperty(property);

        List<Property> found = manager.findPropertyByPrice(new BigDecimal("1000"));
        assertThat(found).isEmpty();
    }

    /* Update operation tests */


    @Test(expected = IllegalArgumentException.class)
    public void updateNullProperty() {
        manager.updateProperty(null);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithNullID() {
        Property property = testingPropertyBuilder().id(null).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithNullArea() {
        Property property = testingPropertyBuilder().id(1L).area(null).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithNegativeArea() {
        Property property = testingPropertyBuilder().id(19L).area(new BigDecimal("-1")).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithZeroArea() {
        Property property = testingPropertyBuilder().id(1L).area(new BigDecimal("0")).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithNullPrice() {
        Property property = testingPropertyBuilder().id(15L).price(null).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithNegativePrice() {
        Property property = testingPropertyBuilder().id(11L).price(new BigDecimal("-1")).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithZeroPrice() {
        Property property = testingPropertyBuilder().id(20L).price(new BigDecimal("0")).buildProperty();
        manager.updateProperty(property);
    }


    @Test(expected = IllegalEntityException.class)
    public void updatePropertyWithNullType() {
        Property property = testingPropertyBuilder().id(9L).type(null).buildProperty();
        manager.updateProperty(property);
    }


    @Test
    public void updatePropertyPrice() {
        Property property = testingPropertyBuilder().buildProperty();
        Property anotherProperty = anotherTestingPropertyBuilder().buildProperty();
        manager.createProperty(property);
        manager.createProperty(anotherProperty);

        property.setPrice(new BigDecimal("2050000"));
        manager.updateProperty(property);

        List<Property> properties = manager.getAllProperties();
        assertThat(properties.contains(property));
    }


    @Test
    public void updatePropertyNonEmptyDatabase() {
        Property property = testingPropertyBuilder().buildProperty();
        Property anotherProperty = anotherTestingPropertyBuilder().buildProperty();

        manager.createProperty(property);
        manager.createProperty(anotherProperty);

        anotherProperty.setAddress("Mulholland Drive 2, Los Angeles");
        manager.updateProperty(anotherProperty);

        List<Property> properties = manager.getAllProperties();
        assertThat(properties.contains(anotherProperty));
    }


    /* Get all properties operation tests */

    @Test
    public void getAllPropertiesFromEmptyDatabase() {
        List<Property> propertyList = manager.getAllProperties();
        assertThat(propertyList).isEmpty();
    }


    @Test
    public void getAllPropertiesNonEmpty() {
        Property first = testingPropertyBuilder().buildProperty();
        Property second = anotherTestingPropertyBuilder().buildProperty();

        manager.createProperty(first);
        manager.createProperty(second);

        List<Property> properties = manager.getAllProperties();
        assertThat(properties).containsOnly(first, second);
    }


}
