package backend;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents relationship between client and property.
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class Contract {
    private Long id;
    private Property property;
    private Client client;
    private LocalDate dateOfSigning;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getDateOfSigning() {
        return dateOfSigning;
    }

    public void setDateOfSigning(LocalDate dateOfSigning) {
        this.dateOfSigning = dateOfSigning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(id, contract.id) &&
                Objects.equals(property, contract.property) &&
                Objects.equals(client, contract.client) &&
                Objects.equals(dateOfSigning, contract.dateOfSigning);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, property, client, dateOfSigning);
    }
}
