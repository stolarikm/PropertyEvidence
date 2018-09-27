import backend.Client;
import backend.Contract;
import backend.Property;

import java.time.LocalDate;

/**
 * Builder class for contract entities
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ContractBuilder {
    private Property property;
    private Client client;
    private LocalDate dateOfSigning;


    public ContractBuilder property(Property property) {
        this.property = property;
        return this;
    }

    public ContractBuilder client(Client client) {
        this.client = client;
        return this;
    }

    public ContractBuilder dateOfSigning(LocalDate date) {
        this.dateOfSigning = date;
        return this;
    }


    public Contract buildContract() {
        Contract contract = new Contract();
        contract.setClient(client);
        contract.setProperty(property);
        contract.setDateOfSigning(dateOfSigning);
        return contract;
    }
}
