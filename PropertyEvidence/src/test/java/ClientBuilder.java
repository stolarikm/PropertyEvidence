import backend.Client;

/**
 * Builder class for client entities
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ClientBuilder {
    private Long id;
    private String fullName;
    private String phoneNumber;


    public ClientBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ClientBuilder fullName(String name) {
        this.fullName = name;
        return this;
    }

    public ClientBuilder phoneNumber(String number) {
        this.phoneNumber = number;
        return this;
    }


    public Client buildClient() {
        Client client = new Client();
        client.setId(id);
        client.setFullName(fullName);
        client.setPhoneNumber(phoneNumber);
        return client;
    }
}
