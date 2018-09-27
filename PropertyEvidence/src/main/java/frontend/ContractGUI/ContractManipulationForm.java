package frontend.ContractGUI;

import backend.*;
import common.IllegalEntityException;
import frontend.MainForm;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * A class representing the window for manipulation with contracts
 * @author Martin Balucha
 */
public class ContractManipulationForm {
    private JComboBox clientComboBox;
    private JComboBox propertyComboBox;
    private JComboBox monthComboBox;
    private JComboBox dayComboBox;
    private JComboBox yearComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    private JPanel contractManipulationPanel;
    private JTextField clientSearchField;
    private JTextField propertySearchField;
    private JLabel propertyAddressLabel;
    private JLabel dateOfSigningLabel;
    private JLabel clientSNameLabel;
    private JLabel contractManipulationLabel;
    private JFrame manipulationFrame;

    private ContractForm rootWindow;
    private ContractManager contractManager;
    private ResourceBundle messages;


    public ContractManipulationForm(ContractForm rootWindow, Contract selectedContract) {
        this.rootWindow = rootWindow;
        contractManager = rootWindow.getContractManager();
        saveButton.addActionListener(actionEvent -> {
            if (selectedContract == null) {
                createContract();
            } else {
                updateContract(selectedContract.getId());
            }
        });

        cancelButton.addActionListener(actionEvent -> manipulationFrame.dispose());
        monthComboBox.setModel(new DefaultComboBoxModel(java.time.Month.values()));
        dayComboBox.setModel(new DefaultComboBoxModel(daysInMonth().toArray()));
        yearComboBox.setModel(new DefaultComboBoxModel(yearSpan().toArray()));
        propertySearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                findProperties();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                findProperties();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                findProperties();
            }
        });
        clientSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                findClients();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                findClients();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                findClients();
            }
        });

        findClients();
        findProperties();

        Locale language = Locale.getDefault();
        messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);
        setGUILanguage();
    }

    /**
     * Translates text labels of GUI components to local language
     */
    private void setGUILanguage() {
        contractManipulationLabel.setText(messages.getString("contractTitle"));
        propertyAddressLabel.setText(messages.getString("contractAddress"));
        dateOfSigningLabel.setText(messages.getString("contractDate"));
        clientSNameLabel.setText(messages.getString("contractName"));
        saveButton.setText(messages.getString("save"));
        cancelButton.setText(messages.getString("cancel"));
    }

    /**
     * Creates list of available years for yearComboBox
     * @return a list of years available for evidence of the contract
     */
    private List<Integer> yearSpan() {
        final int difference = 100;
        List<Integer> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int lowerBoundYEar = currentYear - difference;

        for (Integer i = currentYear; i >= lowerBoundYEar; i--) {
            years.add(i);
        }
        return years;
    }


    /**
     * Creates a list of available days in a month when the contract could be signed
     *
     * @return a list of days in a month
     */
    private List<Integer> daysInMonth() {
        List<Integer> days = new ArrayList<>();
        for (Integer i = 1; i <= 31; i++) {
            days.add(i);
        }
        return days;
    }


    /**
     * Checks is the date is valid
     *
     * @param date date which is to be checked
     * @throws ParseException thrown if the date is invalid
     */
    private static void validateDate(String date) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        df.parse(date);
    }


    /**
     * Adds contract to the database
     */
    private void createContract() {
        Contract contract = new Contract();
        try {
            fillContractWithValuesFromComboBoxes(contract);
            contractManager.createContract(contract);
            rootWindow.getTableModel().addContractToTable(contract);
            rootWindow.getTableModel().fireTableDataChanged();
            manipulationFrame.dispose();
        } catch (IllegalEntityException | DateTimeParseException ex) {
            MainForm.ErrorMessage(manipulationFrame, ex.getMessage());
        } catch (ParseException ex) {
            MainForm.ErrorMessage(manipulationFrame, messages.getString("contractInvalidDate"));
        }
    }


    /**
     * Updates an existing contract in the database
     * @param id id of the selected contract
     */
    private void updateContract(long id) {
        Contract contract = new Contract();
        contract.setId(id);
        try {
            fillContractWithValuesFromComboBoxes(contract);
            contractManager.updateContract(contract);
            rootWindow.getTableModel().updateContractInTable(contract);
            manipulationFrame.dispose();
        } catch (IllegalEntityException | NumberFormatException ex) {
            MainForm.ErrorMessage(manipulationFrame, ex.getMessage());
        } catch (ParseException ex) {
            MainForm.ErrorMessage(manipulationFrame, messages.getString("contractInvalidDate"));
        }
    }


    /**
     * Sets comboBox values to the attribute values of the contract
     *
     * @param contract contract with values which are to be set in comboBoxes
     */
    public void fillComboBoxesWithContractValues(Contract contract) {
        clientComboBox.setSelectedItem(contract.getClient());
        propertyComboBox.setSelectedItem(contract.getProperty());
        monthComboBox.setSelectedItem(contract.getDateOfSigning().getMonth());
        dayComboBox.setSelectedItem(contract.getDateOfSigning().getDayOfMonth());
        yearComboBox.setSelectedItem(contract.getDateOfSigning().getYear());
    }

    /**
     * Fills clientComboBox with clients whose name contain string typed in the clientSearchField
     */
    private void findClients() {
        String clientName = clientSearchField.getText().trim().toLowerCase();
        new SwingWorker<Void, Void>() {
            private List<Client> clientsWithMatchingName = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                clientsWithMatchingName = rootWindow.getClientManager().findClientsByName(clientName);
                return null;
            }

            @Override
            protected void done() {
                clientComboBox.setModel(new DefaultComboBoxModel(clientsWithMatchingName.toArray()));
            }
        }.execute();
    }


    /**
     * Fills propertyComboBox with properties which have had its address typed in the propertySearchField
     */
    private void findProperties() {
        String propertyAddress = propertySearchField.getText().trim().toLowerCase();
        new SwingWorker<Void, Void>() {
            private List<Property> propertiesWithSimilarAddress = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                propertiesWithSimilarAddress = rootWindow.getPropertyManager().findPropertyByAddress(propertyAddress);
                return null;
            }

            @Override
            protected void done() {
                propertyComboBox.setModel(new DefaultComboBoxModel(propertiesWithSimilarAddress.toArray()));
            }
        }.execute();
    }

    /**
     * Corrects the day or month format by adding 0 if it consists only of one digit
     * @param number the day or month number
     * @return  a format of the date with two digits
     */
    private String correctDateFormat(String number) {
        if ((number.length() == 1)) {
            number = "0" + number;
        }
        return number;
    }

    /**
     * Fills contract with the values extracted from the manipulation form
     *
     * @param contract a contract with filled values
     * @throws ParseException if the inserted date is invalid
     */
    private void fillContractWithValuesFromComboBoxes(Contract contract) throws ParseException {
        contract.setClient((Client) clientComboBox.getSelectedItem());
        contract.setProperty((Property) propertyComboBox.getSelectedItem());

        int month = Month.valueOf(monthComboBox.getSelectedItem().toString()).getValue();
        String day = dayComboBox.getSelectedItem().toString();
        String year = yearComboBox.getSelectedItem().toString();
        String date = year + "-" + correctDateFormat(String.valueOf(month)) + "-" + correctDateFormat(day);
        validateDate(date);

        contract.setDateOfSigning(LocalDate.parse(date));
    }


    /**
     * Opens contract manipulation window
     */
    public void openWindow() {
        manipulationFrame = new JFrame(messages.getString("contractManagementTitle"));
        manipulationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        manipulationFrame.setContentPane(contractManipulationPanel);
        manipulationFrame.setPreferredSize(new Dimension(800, 600));
        manipulationFrame.pack();
        manipulationFrame.setVisible(true);
    }
}