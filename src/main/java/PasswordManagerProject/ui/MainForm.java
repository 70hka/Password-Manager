package PasswordManagerProject.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainForm extends JPanel {

    public MainForm() {
        setLayout(new BorderLayout()); // Root Layout

        // ======= Create Tabbed Pane =======
        JTabbedPane tabbedPane = new JTabbedPane();

        // ======= Create Accounts Panel =======
        JPanel accountsPanel = new JPanel(new BorderLayout());

        // ======= Top Panel (Buttons + PIN) =======
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Account");
        JButton removeButton = new JButton("Remove Account");
        JLabel pinLabel = new JLabel("Unhide PIN:");
        JPasswordField pinField = new JPasswordField(10);

        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(pinLabel);
        topPanel.add(pinField);

        // ======= Center Split Pane =======
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        splitPane.setContinuousLayout(true);

        // ======= Left (Table) =======
        String[] columnNames = {"Username", "Password", "Notes"};
        Object[][] data = {
                {"user1", "*****", "Personal"},
                {"user2", "*****", "Work"}
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable accountTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(accountTable);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);

        // ======= Right (Properties - placeholder) =======
        JPanel rightPanel = new JPanel(new BorderLayout());
        JLabel propertiesLabel = new JLabel("Select an account to view website");
        rightPanel.add(propertiesLabel, BorderLayout.NORTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        // ======= Assemble Accounts Panel =======
        accountsPanel.add(topPanel, BorderLayout.NORTH);
        accountsPanel.add(splitPane, BorderLayout.CENTER);

        // ======= Add Tab =======
        tabbedPane.addTab("Accounts", accountsPanel);

        // ======= Add TabbedPane to Main Panel =======
        add(tabbedPane, BorderLayout.CENTER);
    }
}
