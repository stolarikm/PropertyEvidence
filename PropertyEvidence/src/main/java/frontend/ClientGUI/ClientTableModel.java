package frontend.ClientGUI;

import backend.Client;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Custom table model used to display Client objects
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ClientTableModel extends AbstractTableModel {
    private List<Client> clients = new ArrayList<>();
    private String[] columns;

    ClientTableModel() {
        Locale language = Locale.getDefault();
        ResourceBundle messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);
        columns = new String[] {"Id", messages.getString("clientName"), messages.getString("clientPhone")};
    }

    /**
     * Returns name of the column by index
     *
     * @param index wanted index
     * @return name of column
     */
    public String getColumnName(int index) {
        return columns[index];
    }


    /**
     * Adds client to table
     *
     * @param client Client to add
     */
    public void addClientToTable(Client client, boolean updateChanges) {
        clients.add(client);
        if (updateChanges) {
            fireTableDataChanged();
        }
    }

    /**
     * Deletes client from table
     *
     * @param selectedRow number of row containing Client to be deleted
     */
    public void deleteClientFromTable(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < clients.size()) {
            clients.remove(selectedRow);
            fireTableRowsDeleted(selectedRow, selectedRow);
        }
    }

    /**
     * Updates client in table
     *
     * @param client Client to be updated
     */
    public void updateClientInTable(Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getId().equals(client.getId())) {
                clients.set(i, client);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
    }

    /**
     * Clears contents of table
     */
    public void resetTable() {
        clients.clear();
    }

    @Override
    public int getRowCount() {
        return clients.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }


    @Override
    public Object getValueAt(int row, int col) {
        Client client = clients.get(row);
        switch (col) {
            case 0:
                return client.getId();
            case 1:
                return client.getFullName();
            case 2:
                return client.getPhoneNumber();
            default:
                throw new IllegalArgumentException("Column index is out of range");
        }
    }
}



