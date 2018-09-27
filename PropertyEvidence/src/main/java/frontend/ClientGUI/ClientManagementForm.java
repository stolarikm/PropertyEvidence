package frontend.ClientGUI;

import backend.Client;
import backend.ClientManager;
import common.IllegalEntityException;
import frontend.MainForm;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * GUI form for creating and updating clients.
 *
 * @author Michal Stolárik 456173@mail.muni.cz
 */
public class ClientManagementForm {
    private JTextField firstNameField;
    private JTextField surnameField;
    private JTextField phoneField;
    private JButton saveButton;
    private JPanel clientManagementPanel;
    private JLabel firstNameLabel;
    private JLabel surnameLabel;
    private JLabel clientManagementLabel;
    private JLabel telephoneNumberLabel;
    private JButton cancelButton;

    private JFrame frame;
    private ClientForm parent;
    private ClientManager manager;
    private ResourceBundle messages;

    ClientManagementForm(ClientForm parent, Client clientToUpdate) {
        Locale language = Locale.getDefault();
        messages = ResourceBundle.getBundle("Localization" + File.separator + "MessagesBundle", language);

        this.parent = parent;
        this.manager = parent.getManager();

        cancelButton.addActionListener(actionEvent -> frame.dispose());
        saveButton.addActionListener(e -> {
            if (clientToUpdate == null) {
                createClientFromForm();
            } else {
                updateClientFromForm(clientToUpdate.getId());
            }
        });

        if (clientToUpdate != null) {
            firstNameField.setText(clientToUpdate.getFullName().split(" ")[0]);
            surnameField.setText(clientToUpdate.getFullName().split(" ")[1]);
            phoneField.setText(clientToUpdate.getPhoneNumber());
        }
        setGUILanguage();
    }

    /**
     * Translates text labels of GUI components to local language
     */
    private void setGUILanguage() {
        clientManagementLabel.setText(messages.getString("clientManagementTitle"));
        firstNameLabel.setText(messages.getString("clientFirstName"));
        surnameLabel.setText(messages.getString("clientSurname"));
        telephoneNumberLabel.setText(messages.getString("clientPhone"));
        saveButton.setText(messages.getString("save"));
        cancelButton.setText(messages.getString("cancel"));
    }

    /**
     * Opens this form in a new window
     */
    public void openWindow() {
        frame = new JFrame(messages.getString("clientManagementTitle"));
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(clientManagementPanel);
        frame.setPreferredSize(new Dimension(500, 600));
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Updates client with changed parameters from pre-filled form
     *
     * @param id primary key of a stored Client
     */
    private void updateClientFromForm(long id) {
        Client client = new Client();
        client.setFullName(firstNameField.getText() + " " + surnameField.getText());
        client.setPhoneNumber(phoneField.getText());
        client.setId(id);

        new SwingWorker<Void, Void>() {
            String errorMessage;
            @Override
            protected Void doInBackground() {
                try {
                    manager.updateClient(client);
                } catch (IllegalEntityException ex) {
                    errorMessage = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage == null) {
                    parent.getTableModel().updateClientInTable(client);
                    frame.dispose();
                } else {
                    MainForm.ErrorMessage(frame, errorMessage);
                }
            }
        }.execute();
    }

    /**
     * Creates client with parameters filled in form
     */
    private void createClientFromForm() {
        Client client = new Client();
        client.setFullName(firstNameField.getText() + " " + surnameField.getText());
        client.setPhoneNumber(phoneField.getText());

        new SwingWorker<Void, Void>() {
            String errorMessage;
            @Override
            protected Void doInBackground() {
                try {
                    manager.createClient(client);
                } catch (IllegalEntityException ex) {
                    errorMessage = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (errorMessage == null) {
                    parent.getTableModel().addClientToTable(client, true);
                    frame.dispose();
                } else {
                    MainForm.ErrorMessage(frame, errorMessage);
                }
            }
        }.execute();
    }
}
