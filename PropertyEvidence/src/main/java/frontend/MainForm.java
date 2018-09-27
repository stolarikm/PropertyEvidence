package frontend;

import backend.Main;
import frontend.ClientGUI.ClientForm;
import frontend.ContractGUI.ContractForm;
import frontend.PropertyGUI.PropertyForm;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;

public class MainForm {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;

    public JTabbedPane getPanel() { return tabbedPane1; }

    private void createUIComponents() {
        DataSource dataSource = /*Main.connectToStandaloneDatabase()*/ Main.createMemoryDatabase();

        tabbedPane1 = new JTabbedPane();
        tabbedPane1.add("Client", new ClientForm(dataSource).getPanel());
        tabbedPane1.add("Property", new PropertyForm(dataSource).getInitialPropertyPanel());
        tabbedPane1.add("Contract", new ContractForm(dataSource).getInitialContractPanel());
    }

    /**
     * Opens an dialog with error message
     *
     * @param parent parent element which thrown this error
     * @param message error message to be displayed in dialog
     */
    public static void ErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }
}
