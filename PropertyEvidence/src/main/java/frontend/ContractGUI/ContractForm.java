package frontend.ContractGUI;

import backend.*;
import frontend.MainForm;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A class representing form for evidence of the contracts
 * @author Martin Balucha
 */
public class ContractForm {
    private JPanel initialContractPanel;
    private JTable contractTable;
    private JButton addContractButton;
    private JButton removeContractButton;
    private JButton updateContractButton;
    private JTextField searchByClientField;
    private JTextField searchByPropertyField;
    private JLabel contractEvidenceLabel;
    private JLabel searchByClientLabel;
    private JLabel searchByPropertyLabel;


    private ContractTableModel tableModel;
    private ContractManager contractManager;
    private PropertyManager propertyManager;
    private ClientManager clientManager;
    private ResourceBundle messages;


    public ContractForm(DataSource dataSource) {
        contractManager = new ContractManagerImpl(dataSource);
        propertyManager = new PropertyManagerImpl(dataSource);
        clientManager = new ClientManagerImpl(dataSource);

        addContractButton.addActionListener(actionEvent -> new ContractManipulationForm(this, null).openWindow());
        removeContractButton.addActionListener(actionEvent -> deleteContract(contractTable.getSelectedRow()));
        updateContractButton.addActionListener(actionEvent -> updateContract(contractTable.getSelectedRow()));
        searchByClientField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                searchByClient();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                searchByClient();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                searchByClient();
            }
        });
        searchByPropertyField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                searchByProperty();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                searchByProperty();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                searchByProperty();
            }
        });

        Locale language = Locale.getDefault();
        messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);
        setGUILanguage();
    }

    /**
     * Translates text labels of GUI components to local language
     */
    private void setGUILanguage() {
        contractEvidenceLabel.setText(messages.getString("contractTitle"));
        searchByClientLabel.setText(messages.getString("contractSearchByClient"));
        searchByPropertyLabel.setText(messages.getString("contractSearchByProperty"));
        addContractButton.setText(messages.getString("contractCreate"));
        removeContractButton.setText(messages.getString("contractRemove"));
        updateContractButton.setText(messages.getString("contractUpdate"));
    }


    /**
     * Deletes contract from the evidence
     * @param selectedRow row of the table which is to be deleted
     */
    private void deleteContract(int selectedRow) {
        if (!tableModel.getColumnMap().containsKey(selectedRow)) {
            MainForm.ErrorMessage(null, messages.getString("contractNotSelected"));
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Contract contractToBeDeleted = contractManager.getContractById(id);
        contractManager.deleteContract(contractToBeDeleted);
        tableModel.deleteContractFromTable(selectedRow);
    }



    /**
     * Updates contract in the table
     * @param selectedRow row of the specific contract
     */
    private void updateContract(int selectedRow) {
        if (!tableModel.getColumnMap().containsKey(selectedRow)) {
            MainForm.ErrorMessage(null, messages.getString("contractNotSelected"));
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        Contract contractToBeUpdated = contractManager.getContractById(id);
        ContractManipulationForm form = new ContractManipulationForm(this, contractToBeUpdated);
        form.fillComboBoxesWithContractValues(contractToBeUpdated);
        form.openWindow();
    }

    /**
     * Displays all contracts in the evidence to the table
     */
    private void displayAllContractsInTable() {
        List<Contract> contractList = contractManager.getAllContracts();
        for (Contract contract : contractList) {
            tableModel.addContractToTable(contract);
        }
    }


    /**
     * Displays all contracts of the clients with the name containing the string in the clientTextField
     */
    private void searchByClient() {
        tableModel.resetTable();
        if (searchByClientField.getText().trim().isEmpty()) {
            displayAllContractsInTable();
            return;
        }
        new SwingWorker<Void, Void>() {
            private List<Contract> contracts = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                String clientName = searchByClientField.getText();
                List<Client> clientsWithSameName = clientManager.findClientsByName(clientName);
                for (Client client : clientsWithSameName) {
                    contracts.addAll(contractManager.findContractsByClient(client));
                }
                return null;
            }

            @Override
            protected void done() {
                for (Contract contract : contracts) {
                    tableModel.addContractToTable(contract);
                }
                tableModel.fireTableDataChanged();
            }

        }.execute();
    }


    /**
     * Finds all contracts with the property situated on the address given in the propertyTextField
     */
    private void searchByProperty() {
        tableModel.resetTable();
        if (searchByClientField.getText().trim().isEmpty()) {
            displayAllContractsInTable();
            return;
        }
        new SwingWorker<Void, Void>() {
            private List<Contract> contracts = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                String propertyAddress = searchByClientField.getText();
                List<Property> propertiesWithTheSameAddress = propertyManager.findPropertyByAddress(propertyAddress);
                for (Property property : propertiesWithTheSameAddress) {
                    contracts.addAll(contractManager.findContractByProperty(property));
                }
                return null;
            }

            @Override
            protected void done() {
                for (Contract contract : contracts) {
                    tableModel.addContractToTable(contract);
                }
                tableModel.fireTableDataChanged();
            }

        }.execute();
    }


    public JPanel getInitialContractPanel() {
        return initialContractPanel;
    }

    public ContractTableModel getTableModel() {
        return tableModel;
    }

    public ContractManager getContractManager() {
        return contractManager;
    }


    public PropertyManager getPropertyManager() {
        return propertyManager;
    }


    public ClientManager getClientManager() {
        return clientManager;
    }

    private void createUIComponents() {
        contractTable = new JTable();
        contractTable.setModel(new ContractTableModel());
        tableModel = (ContractTableModel) contractTable.getModel();
    }


}
