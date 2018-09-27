import backend.PropertyType;
import backend.Property;
import java.math.BigDecimal;


/**
 * A class representing property entity
 * @author Martin Balucha
 */
public class PropertyBuilder {
    private Long id;
    private BigDecimal area;
    private BigDecimal price;
    private String address;
    private PropertyType type;


    public PropertyBuilder id(Long id) {
        this.id = id;
        return this;
    }


    public PropertyBuilder area(BigDecimal area) {
        this.area = area;
        return this;
    }


    public PropertyBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }


    public PropertyBuilder address(String address) {
        this.address = address;
        return this;
    }


    public PropertyBuilder type(PropertyType type) {
        this.type = type;
        return this;
    }


    public Property buildProperty() {
        Property property = new Property();
        property.setAddress(address);
        property.setArea(area);
        property.setId(id);
        property.setPrice(price);
        property.setType(type);
        return property;
    }
}
