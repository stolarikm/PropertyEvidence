package frontend.PropertyGUI;

import backend.Property;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.*;

/**
 * Represents table of the properties
 * @author Martin Balucha
 */
public class PropertyTableModel extends AbstractTableModel {
    private List<Property> properties = new ArrayList<>();
    private Map<Integer, String> columnMap;

    public PropertyTableModel() {
        Locale language = Locale.getDefault();
        ResourceBundle messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);
        columnMap = columnMapInit(messages);
    }

    /**
     * Returns name of the column according to the index in the table
     * @param columnIndex
     * @return
     */
    public String getColumnName(int columnIndex) {
        return columnMap.get(columnIndex);
    }

    /**
     * Adds new property to the table
     * @param property
     */
    public void addPropertyToTable(Property property) {
        properties.add(property);
        fireTableDataChanged();
    }

    /**
     * Deletes selected row from the table
     * @param selectedRow   row which is to be deleted
     */
    public void deletePropertyFromTable(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < properties.size()) {
            properties.remove(selectedRow);
        }
    }


    public void updatePropertyInTable(Property property) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).getId().equals(property.getId())) {
                properties.set(i, property);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
    }

    @Override
    public int getRowCount() {
        return properties.size();
    }

    @Override
    public int getColumnCount() {
        return columnMap.size();
    }


    /**
     * Resets table
     */
    public void resetTable() {
        properties.clear();
    }


    @Override
    public Object getValueAt(int row, int col) {
        Property property = properties.get(row);
        switch (col) {
            case 0:
                return property.getId();
            case 1:
                return property.getAddress();
            case 2:
                return property.getArea();
            case 3:
                return property.getPrice();
            case 4:
                return property.getType();
            default:
                throw new IllegalArgumentException("Column index is out of range");
        }
    }



    /**
     * Initiates table of the cars.
     * @return A map of columns and their values
     */
    private Map<Integer, String> columnMapInit(ResourceBundle messages) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "Id");
        map.put(1, messages.getString("propertyAddress"));
        map.put(2, messages.getString("propertyArea"));
        map.put(3, messages.getString("propertyPrice"));
        map.put(4, messages.getString("propertyType"));
        return map;
    }


    public Map<Integer, String> getColumnMap() {
        return new HashMap<>(columnMap);
    }

}
