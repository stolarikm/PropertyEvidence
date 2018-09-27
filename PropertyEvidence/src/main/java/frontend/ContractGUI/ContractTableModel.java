package frontend.ContractGUI;

import backend.Contract;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.*;

/**
 * A class representing table of the contracts
 * @author Martin Balucha
 */
public class ContractTableModel extends AbstractTableModel {
    private List<Contract> contracts = new ArrayList<>();
    private Map<Integer, String> columnMap;

    public ContractTableModel() {
        Locale language = Locale.getDefault();
        ResourceBundle messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);
        columnMap = columnMapInit(messages);
    }

    @Override
    public int getRowCount() {
        return contracts.size();
    }


    @Override
    public int getColumnCount() {
        return columnMap.size();
    }


    /**
     * Returns name of the column according to the index in the table
     * @param columnIndex index of the specific column
     * @return name of the column
     */
    public String getColumnName(int columnIndex) {
        return columnMap.get(columnIndex);
    }


    @Override
    public Object getValueAt(int row, int column) {
        Contract contract = contracts.get(row);
        switch (column) {
            case 0:
                return contract.getId();
            case 1:
                return contract.getProperty();
            case 2:
                return contract.getClient();
            case 3:
                return contract.getDateOfSigning();
            default:
                throw new IllegalArgumentException("Column index is out of range");
        }
    }


    /**
     * Adds new contract to the table
     * @param contract  contract which is to be added to the table
     */
    public void addContractToTable(Contract contract) {
        contracts.add(contract);
        fireTableDataChanged();
    }


    /**
     * Deletes contract from the table
     * @param selectedRow   index of the contract which is to be deleted
     */
    public void deleteContractFromTable(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < contracts.size()) {
            contracts.remove(selectedRow);
            fireTableRowsDeleted(selectedRow, selectedRow);
        }
    }


    /**
     * Updates contract in the table
     * @param contract contract which was changed
     */
    public void updateContractInTable(Contract contract) {
        for (int i = 0; i < contracts.size(); i++) {
            if (contracts.get(i).getId().equals(contract.getId())) {
                contracts.set(i, contract);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
    }


    /**
     * Resets the table
     */
    public void resetTable() {
        contracts.clear();
    }


    /**
     * Initializes the map matching columns and their names
     * @return a fully initialized map of columns and names
     */
    private Map<Integer, String> columnMapInit(ResourceBundle messages) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Id");
        map.put(1, messages.getString("contractProperty"));
        map.put(2, messages.getString("contractClient"));
        map.put(3, messages.getString("contractDate"));
        return map;
    }


    public Map<Integer, String> getColumnMap() {
        return new HashMap<>(columnMap);
    }

}
