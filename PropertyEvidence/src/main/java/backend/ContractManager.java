package backend;

import java.util.List;

/**
 * Interface for manipulation with contracts.
 *
 * @author Michal Stolárik 456173@mail.muni.cz, Martin Balucha
 */

public interface ContractManager {


    /**
     * Adds new contract to the evidence
     * @param contract  a contract which is to be created
     * @throws IllegalArgumentException when Property, Client or date of signing are null
     */
    void createContract(Contract contract);


    /**
     * Removes contract from the evidence
     * @param contract  a contract which is to be removed from the database
     * @throws common.IllegalEntityException when the property from the parameter is not in the database
     */
    void deleteContract(Contract contract);


    /**
     * Returns a list of all contracts in the database
     * @return  a list of all contracts
     */
    List<Contract> getAllContracts();


    /**
     * Updates information on the already existing contract
     * @param contract  a contract which is updated
     * @throws common.IllegalEntityException when the update is invalid
     */
    void updateContract(Contract contract);


    /**
     * Returns all contracts of the given client
     * @param client    client whose contracts are to be returned
     * @return          a list of all client's contracts
     */
    List<Contract> findContractsByClient(Client client);


    /**
     * Returns a contract of the specific property
     * @param property  a property which contract is to be returned
     * @return          a selling contract of the given property
     */
    List<Contract> findContractByProperty(Property property);


    /**
     * Returns a contract with a specific Id
     * @param id id of the wanted contract
     * @return  a contract with the wanted Id. Null if not found
     */
    Contract getContractById(Long id);
}
