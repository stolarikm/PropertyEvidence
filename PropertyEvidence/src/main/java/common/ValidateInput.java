package common;


import backend.Client;
import backend.Contract;
import backend.Property;
import backend.PropertyType;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * A static class providing methods for checking input
 * @author Martin Balucha
 */
public class ValidateInput {
    private static ResourceBundle messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", Locale.getDefault());

    public static void validateProperty(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Property is null");
        }
        if (property.getArea() == null || property.getArea().compareTo(new BigDecimal("0")) <= 0) {
            throw new IllegalEntityException(messages.getString("propertyAreaError"));
        }
        if (property.getPrice() == null || property.getPrice().compareTo(new BigDecimal("0")) <= 0) {
            throw new IllegalEntityException(messages.getString("propertyPriceError"));
        }
        if (property.getType() == null) {
            throw new IllegalEntityException(messages.getString("propertyTypeError"));
        }
        if (property.getAddress() == null || property.getAddress().isEmpty()) {
            throw new IllegalEntityException(messages.getString("propertyAddressError"));
        }
    }


    public static void validateContract(Contract contract) {
        if (contract == null) {
            throw new IllegalArgumentException("Contract is null");
        }
        if (contract.getClient() == null) {
            throw new IllegalEntityException(messages.getString("clientNotSelected"));
        }
        if (contract.getProperty() == null) {
            throw new IllegalEntityException(messages.getString("propertyNotSelected"));
        }
        if (contract.getDateOfSigning() == null) {
            throw new IllegalEntityException("Date of signing in contract is null");
        }
        if (LocalDate.now().isBefore(contract.getDateOfSigning())) {
            throw new IllegalEntityException("Date of signing is in the future");        }
    }

    public static void validateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client is null");
        }
        validateClientsName(client);
        validateClientsPhoneNumber(client);
    }

    private static void validateClientsName(Client client) {
        if (client.getFullName().split(" ").length != 2) {
            throw new IllegalEntityException(messages.getString("clientNumberOfNamesError"));
        }
        if (client.getFullName().matches("^.*[^a-zA-Z ].*$")) {
            throw new IllegalEntityException(messages.getString("clientAccentsError"));
        }
    }

    private static void validateClientsPhoneNumber(Client client) {
        if (client.getPhoneNumber().length() < 10 || client.getPhoneNumber().length() > 13) {
            throw new IllegalEntityException(messages.getString("clientPhoneLengthError"));
        }
        if (client.getPhoneNumber().matches("^.*[^0-9|+ ].*$")) {
            throw new IllegalEntityException(messages.getString("clientPhoneInvalidCharacterError"));
        }
    }
}
