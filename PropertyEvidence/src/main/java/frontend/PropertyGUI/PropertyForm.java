package frontend.PropertyGUI;

import backend.*;
import frontend.ClientGUI.ClientTableModel;
import frontend.MainForm;
import org.apache.derby.iapi.services.i18n.BundleFinder;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Represents property menu with table of all properties in the database and buttons for CRUD operations
 * @author Martin Balucha
 */
public class PropertyForm {
    private JTable propertyTable;
    private JButton addPropertyButton;
    private JButton removePropertyButton;
    private JButton updatePropertyButton;
    private JPanel initialPropertyPanel;
    private JTextField searchByAddressTextField;
    private JTextField searchByPriceTextField;
    private JLabel propertyEvidenceLabel;
    private JLabel searchByPriceLabel;
    private JLabel searchByAddressLabel;

    private PropertyTableModel tableModel;
    private PropertyManager manager;
    private ContractManager contractManager;
    private ResourceBundle messages;

    public PropertyForm(DataSource dataSource) {
        manager = new PropertyManagerImpl(dataSource);
        contractManager = new ContractManagerImpl(dataSource);

        Locale language = Locale.getDefault();
        messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);

        tableModel = (PropertyTableModel) propertyTable.getModel();
        addPropertyButton.addActionListener(event -> new PropertyManipulationForm(this, null).openWindow());
        removePropertyButton.addActionListener(actionEvent -> deleteProperty(propertyTable.getSelectedRow()));
        updatePropertyButton.addActionListener(actionEvent -> updateProperty(propertyTable.getSelectedRow()));
        searchByAddressTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                searchByAddress();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                searchByAddress();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                searchByAddress();
            }
        });
        searchByPriceTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                searchByPrice();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                searchByPrice();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                searchByPrice();
            }
        });

        setGUILanguage();
    }

    /**
     * Translates text labels of GUI components to local language
     */
    private void setGUILanguage() {
        propertyEvidenceLabel.setText(messages.getString("propertyTitle"));
        searchByPriceLabel.setText(messages.getString("propertySearchByPrice"));
        searchByAddressLabel.setText(messages.getString("propertySearchByAddress"));
        addPropertyButton.setText(messages.getString("propertyCreate"));
        removePropertyButton.setText(messages.getString("propertyRemove"));
        updatePropertyButton.setText(messages.getString("propertyUpdate"));
    }

    /**
     * Deletes property from the database
     *
     * @param selectedRow row of the table which is to be deleted
     */
    private void deleteProperty(int selectedRow) {
        if (selectedRow == -1) {
            MainForm.ErrorMessage(null, messages.getString("propertyNotSelected"));
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);

        new SwingWorker<Void, Void>() {
            private boolean deleteSuccessful = true;
            @Override
            protected Void doInBackground() {
                Property propertyToBeDeleted = manager.getPropertyById(id);
                if (!propertyHasContracts(propertyToBeDeleted)) {
                    manager.deleteProperty(propertyToBeDeleted);
                    return null;
                }
                deleteSuccessful = false;
                return null;
            }

            @Override
            protected void done() {
                if (deleteSuccessful) {
                    tableModel.deletePropertyFromTable(selectedRow);
                    tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
                    return;
                }
                MainForm.ErrorMessage(null, messages.getString("propertyDeleteUnsuccessful"));
            }
        }.execute();
    }


    /**
     * Opens PropertyManipulationForm where the new values of the property are set and saved
     *
     * @param selectedRow row of the table which is to be updated
     */
    private void updateProperty(int selectedRow) {
        if (selectedRow == -1) {
            MainForm.ErrorMessage(null, messages.getString("propertyNotSelected"));
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        PropertyForm outerClass = this;

        new SwingWorker<Void, Void>() {
            private Property propertyToBeUpdated;

            @Override
            protected Void doInBackground() {
                propertyToBeUpdated = manager.getPropertyById(id);
                return null;
            }

            @Override
            protected void done() {
                PropertyManipulationForm form = new PropertyManipulationForm(outerClass, propertyToBeUpdated);
                form.fillTextFieldsWithPropertyValues(propertyToBeUpdated);
                form.openWindow();
            }
        }.execute();
    }


    /**
     * Displays all properties with the wanted address in the table
     */
    private void searchByAddress() {
        String wantedAddress = searchByAddressTextField.getText().trim();
        tableModel.resetTable();
        if (wantedAddress.isEmpty()) {
            displayAllPropertiesInTable();
            return;
        }

        new SwingWorker<Void, Void>() {
            private List<Property> properties = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                properties = manager.findPropertyByAddress(wantedAddress);
                return null;
            }

            @Override
            protected void done() {
                for (Property property : properties) {
                    tableModel.addPropertyToTable(property);
                }
                tableModel.fireTableDataChanged();
            }
        }.execute();
    }


    /**
     * Displays all properties with the wanted address in the table
     */
    private void searchByPrice() {
        tableModel.resetTable();
        if (searchByPriceTextField.getText().trim().isEmpty()) {
            displayAllPropertiesInTable();
            return;
        }

        new SwingWorker<Void, Void>() {
            private List<Property> properties = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                properties = manager.findPropertyByPrice(new BigDecimal(searchByPriceTextField.getText()));
                return null;
            }

            @Override
            protected void done() {
                for (Property property : properties) {
                    tableModel.addPropertyToTable(property);
                }
                tableModel.fireTableDataChanged();
            }

        }.execute();
    }


    /**
     * Displays all properties in the database to the table
     */
    private void displayAllPropertiesInTable() {

        new SwingWorker<Void, Void>() {
            private List<Property> properties = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                properties = manager.getAllProperties();
                return null;
            }

            @Override
            protected void done() {
                for (Property property : properties) {
                    tableModel.addPropertyToTable(property);
                }
            }
        }.execute();
    }

    private boolean propertyHasContracts(Property property) {
        return !contractManager.findContractByProperty(property).isEmpty();
    }

    public JPanel getInitialPropertyPanel() {
        return initialPropertyPanel;
    }


    private void createUIComponents() {
        propertyTable = new JTable();
        PropertyTableModel tableModel = new PropertyTableModel();
        propertyTable.setModel(tableModel);
    }


    public PropertyTableModel getTableModel() {
        return tableModel;
    }

    public PropertyManager getManager() {
        return manager;
    }
}
