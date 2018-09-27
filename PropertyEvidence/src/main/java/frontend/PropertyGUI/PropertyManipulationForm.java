package frontend.PropertyGUI;

import backend.Property;
import backend.PropertyManager;
import backend.PropertyType;
import common.IllegalEntityException;
import frontend.MainForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * A class representing form which enables manager to manipulate with properties
 * @author Martin Balucha
 */
public class PropertyManipulationForm {
    private JTextField addressTextField;
    private JTextField priceTextField;
    private JTextField areaTextField;
    private JComboBox typeComboBox;
    private JButton saveButton;
    private JButton cancelButton;
    private JPanel propertyManipulationPanel;
    private JLabel propertyManipulationLabel;
    private JLabel addressLabel;
    private JLabel areaM2Label;
    private JLabel priceCzkLabel;
    private JLabel typeLabel;
    private JFrame manipulationFrame;

    private PropertyForm rootWindow;
    private PropertyManager manager;
    private ResourceBundle messages;

    public PropertyManipulationForm(PropertyForm rootWindow, Property selectedProperty) {
        Locale language = Locale.getDefault();
        messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);

        this.rootWindow = rootWindow;
        this.manager = rootWindow.getManager();
        saveButton.addActionListener(actionEvent -> {
            if (selectedProperty == null) {
                createProperty();
            }
            else {
                updateProperty(selectedProperty.getId());
            }
        });
        typeComboBox.setModel(new DefaultComboBoxModel(PropertyType.values()));
        cancelButton.addActionListener(actionEvent -> manipulationFrame.dispose());
        initiateDecimalTextFields();

        setGUILanguage();
    }

    /**
     * Translates text labels of GUI components to local language
     */
    private void setGUILanguage() {
        propertyManipulationLabel.setText(messages.getString("propertyManagementTitle"));
        addressLabel.setText(messages.getString("propertyAddress"));
        areaM2Label.setText(messages.getString("propertyArea"));
        priceCzkLabel.setText(messages.getString("propertyPrice"));
        typeLabel.setText(messages.getString("propertyType"));
        saveButton.setText(messages.getString("save"));
        cancelButton.setText(messages.getString("cancel"));
    }


    /**
     * Sets restriction to the area text field and price text field
     */
    private void initiateDecimalTextFields() {
        setTextFieldRestriction(priceTextField);
        setTextFieldRestriction(areaTextField);
    }


    /**
     * Sets text input restriction to the field. Only digits and decimal point can be entered.
     * @param textField
     */
    private void setTextFieldRestriction(JTextField textField) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                super.keyTyped(keyEvent);
                char c = keyEvent.getKeyChar();
                boolean isCharAllowed = (Character.isDigit(c) ||c == '.');
                if (!isCharAllowed && !keyEvent.isAltDown()) {
                    keyEvent.consume();
                }
            };
        });
    }

    /**
     * Creates new property with the values given in the PropertyManipulationForm
     */
    private void createProperty() {
        Property property = new Property();
        try {
            fillPropertyWithTextFieldValues(property);

            new SwingWorker<Void, Void>() {
                String errorMessage;
                @Override
                protected Void doInBackground() {
                    try {
                        manager.createProperty(property);
                    } catch (IllegalEntityException ex) {
                        errorMessage = ex.getMessage();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (errorMessage == null) {
                        rootWindow.getTableModel().addPropertyToTable(property);
                        manipulationFrame.dispose();
                    } else {
                        MainForm.ErrorMessage(manipulationFrame, errorMessage);
                    }
                    rootWindow.getTableModel().fireTableDataChanged();
                }
            }.execute();
        } catch(IllegalEntityException | NumberFormatException ex) {
            MainForm.ErrorMessage(manipulationFrame, ex.getMessage());
        }
    }

    /**
     * Updates an already existing property in the table with given values
     * @param id primary key of the updated property
     */
    private void updateProperty(long id) {
        Property property = new Property();
        property.setId(id);
        fillPropertyWithTextFieldValues(property);
        new SwingWorker<Void, Void>() {
            String errorMessage;
            @Override
            protected Void doInBackground() {
                try {
                    manager.updateProperty(property);
                } catch (IllegalEntityException ex) {
                    errorMessage = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage == null) {
                    rootWindow.getTableModel().updatePropertyInTable(property);
                    manipulationFrame.dispose();
                } else {
                    MainForm.ErrorMessage(manipulationFrame, errorMessage);
                }
            }
        }.execute();
    }


    /**
     * Fills text fields with the values of the chosen property
     * @param property property which has its values filled in text fields
     */
    public void fillTextFieldsWithPropertyValues(Property property) {
        addressTextField.setText(property.getAddress());
        areaTextField.setText(property.getArea().toString());
        priceTextField.setText(property.getPrice().toString());
        typeComboBox.setSelectedItem(property.getType());
    }


    /**
     * Fills property with the values from the text fields
     * @param property  property which has its values set according to the text fields
     */
    private void fillPropertyWithTextFieldValues(Property property) {
        property.setAddress(addressTextField.getText());
        property.setPrice(priceTextField.getText().isEmpty() ? null : new BigDecimal(priceTextField.getText()));
        property.setArea(areaTextField.getText().isEmpty() ? null : new BigDecimal(areaTextField.getText()));
        String type = String.valueOf(typeComboBox.getSelectedItem());
        property.setType(PropertyType.valueOf(type));
    }

    /**
     * Opens PropertyManipulation window
     */
    public void openWindow() {
        manipulationFrame = new JFrame(messages.getString("propertyManagementTitle"));
        manipulationFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        manipulationFrame.setContentPane(propertyManipulationPanel);
        manipulationFrame.setPreferredSize(new Dimension(500, 600));
        manipulationFrame.pack();
        manipulationFrame.setVisible(true);
    }
}
