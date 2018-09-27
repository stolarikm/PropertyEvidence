package frontend.ClientGUI;

import backend.*;
import frontend.MainForm;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * GUI form containing table of clients, an buttons to manage them.
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ClientForm {
    private JButton createButton;
    private JTable clientTable;
    public JPanel clientPanel;
    private JLabel title;
    private JButton editButton;
    private JButton removeButton;
    private JTextField searchBar;
    private JLabel searchByNameLabel;

    private ClientManager manager;
    private ContractManager contractManager;
    private ClientTableModel tableModel;
    private ResourceBundle messages;

    public ClientManager getManager() {
        return manager;
    }
    public ClientTableModel getTableModel() { return tableModel; }
    public JPanel getPanel() { return clientPanel; }

    public ClientForm(DataSource dataSource) {
        manager = new ClientManagerImpl(dataSource);
        contractManager = new ContractManagerImpl(dataSource);

        Locale language = Locale.getDefault();
        messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);

        createButton.addActionListener(e -> new ClientManagementForm(this, null).openWindow());
        editButton.addActionListener(e -> updateClient(clientTable.getSelectedRow()));
        removeButton.addActionListener(e -> removeClient(clientTable.getSelectedRow()));
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                searchClients();
            }
            public void removeUpdate(DocumentEvent e) {
                searchClients();
            }
            public void insertUpdate(DocumentEvent e) {
                searchClients();
            }
        });
        setGUILanguage();
        searchClients();
    }

    /**
     * Translates text labels of GUI components to local language
     */
    private void setGUILanguage() {
        title.setText(messages.getString("clientTitle"));
        searchByNameLabel.setText(messages.getString("clientSearch"));
        createButton.setText(messages.getString("clientCreate"));
        editButton.setText(messages.getString("clientUpdate"));
        removeButton.setText(messages.getString("clientDelete"));
    }

    /**
     * Creates custom JTable for storing clients
     */
    private void createUIComponents() {
        clientTable = new JTable();
        clientTable.setModel(new ClientTableModel());
        tableModel = (ClientTableModel) clientTable.getModel();
    }

    /**
     * Searches clients whose names contains wanted substring
     */
    private void searchClients() {
        String subName = searchBar.getText();

        tableModel.resetTable();

        new SwingWorker<Void, Void>() {
            private List<Client> clients = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                clients = manager.findClientsByName(subName);
                return null;
            }

            @Override
            protected void done() {
                for (Client client: clients) {
                    tableModel.addClientToTable(client, false);
                }
                tableModel.fireTableDataChanged();
            }
        }.execute();
    }

    /**
     * Updates Client on the selectedRow row in table
     *
     * @param selectedRow selected row
     */
    private void updateClient(int selectedRow) {
        if (selectedRow == -1) {
            MainForm.ErrorMessage(null, messages.getString("clientNotSelected"));
            return;
        }
        long id = (long)clientTable.getValueAt(selectedRow, 0);

        ClientForm outerClass = this;

        new SwingWorker<Void, Void>() {
            private Client client;

            @Override
            protected Void doInBackground() {
                client = manager.getClientById(id);
                return null;
            }

            @Override
            protected void done() {
                new ClientManagementForm(outerClass, client).openWindow();
            }
        }.execute();


    }

    /**
     * Removes Client on the selectedRow row from table
     *
     * @param selectedRow selected row
     */
    private void removeClient(int selectedRow) {
        if (selectedRow == -1) {
            MainForm.ErrorMessage(null, messages.getString("clientNotSelected"));
            return;
        }
        long id = (long) clientTable.getValueAt(selectedRow, 0);

        new SwingWorker<Void, Void>() {
            private boolean deleteSuccessful = true;
            @Override
            protected Void doInBackground() {
                Client clientToDelete = manager.getClientById(id);
                if (!clientHasContracts(clientToDelete)) {
                    manager.deleteClient(manager.getClientById(id));
                    return null;
                }
                deleteSuccessful = false;
                return null;
            }

            @Override
            protected void done() {
                if (deleteSuccessful) {
                    tableModel.deleteClientFromTable(selectedRow);
                    return;
                }
                MainForm.ErrorMessage(null, messages.getString("clientDeleteUnsuccessful"));
            }
        }.execute();
    }

    private boolean clientHasContracts(Client client) {
        return !contractManager.findContractsByClient(client).isEmpty();
    }
}
