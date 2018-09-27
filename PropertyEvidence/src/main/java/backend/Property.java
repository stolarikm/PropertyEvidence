package backend;
import java.math.BigDecimal;
import java.util.Objects;


/**
 *A class representing a property entity
 * @author Martin Balucha
 */
public class Property {
    private Long id;
    private BigDecimal area;
    private String address;
    private BigDecimal price;
    private PropertyType type;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }



    @Override
    public boolean equals(Object o) {
        if (o instanceof Property) {
            Property tmp = (Property) o;
            return tmp.getId().equals(id) && tmp.getAddress().equals(address) && tmp.getType().equals(type) && tmp.getArea().equals(area);
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, area, address, price, type);
    }


    @Override
    public String toString() {
        return address + ", area: " + area + " m^2, Czk, type: " + type.toString();
    }
}
