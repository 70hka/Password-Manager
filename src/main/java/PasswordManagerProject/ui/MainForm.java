package PasswordManagerProject.ui;

import PasswordManagerProject.model.Account;
import PasswordManagerProject.storage.AccountStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainForm extends JPanel {

    private DefaultTableModel tableModel;
    private JTable accountTable;
    private List<Account> accounts;

    public MainForm() {
        setLayout(new BorderLayout()); // Root Layout

        // ======= Load Accounts =======
        accounts = AccountStorage.loadAccounts();

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
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        accountTable = new JTable(tableModel);
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

        // ======= Load Table Data =======
        refreshTable();

        // ======= Button Actions =======
        addButton.addActionListener(e -> openAddAccountDialog());
        removeButton.addActionListener(e -> removeSelectedAccount());
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Clear
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{acc.getUsername(), "*****", acc.getNotes()});
        }
    }

    private void openAddAccountDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddAccountDialog dialog = new AddAccountDialog(parentFrame);
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            Account newAcc = dialog.getAccount();
            accounts.add(newAcc);
            AccountStorage.saveAccounts(accounts);
            refreshTable();
        }
    }

    private void removeSelectedAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow != -1) {
            accounts.remove(selectedRow);
            AccountStorage.saveAccounts(accounts);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to remove.");
        }
    }
}
