package PasswordManagerProject.ui;

import PasswordManagerProject.model.Account;
import PasswordManagerProject.storage.AccountStorage;
import PasswordManagerProject.model.AccountData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainForm extends JPanel {

    private DefaultTableModel tableModel;
    private JTable accountTable;
    private List<Account> accounts;

    private JLabel timerLabel;
    private Timer hideTimer;
    private JPasswordField pinField;
    private boolean passwordsVisible = false;
    private JButton hideButton;

    private AccountData accountData;
    private PropertiesPanel propertiesPanel;
    private String pin;

    public MainForm() {
        setLayout(new BorderLayout()); // Root Layout

        // ======= First-time setup or PIN unlock =======
        File accountFile = new File("accounts.json");
        File saltFile = new File("salt.dat");

        if (!accountFile.exists() || !saltFile.exists() || accountFile.length() == 0) {
            // First-time launch → Create new PIN
            while (true) {
                pin = JOptionPane.showInputDialog(this, "Create a new PIN:");
                if (pin == null || pin.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "PIN is required to use the application.");
                    System.exit(0);
                }

                int confirm = JOptionPane.showConfirmDialog(this, "Confirm this PIN?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    accountData = new AccountData(pin, new java.util.ArrayList<>());
                    AccountStorage.saveData(accountData, pin);
                    JOptionPane.showMessageDialog(this, "PIN created and saved.");
                    break;
                }
            }
        } else {
            // ======= Existing user → Prompt to enter PIN =======
            while (true) {
                pin = JOptionPane.showInputDialog(this, "Enter your PIN to unlock:");
                if (pin == null || pin.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "PIN is required to start the application.");
                    System.exit(0);
                }

                accountData = AccountStorage.loadData(pin);
                if (accountData.getPin() == null) {
                    JOptionPane.showMessageDialog(this, "Decryption failed. Wrong PIN or file corrupted.");
                } else if (pin.equals(accountData.getPin())) {
                    break; // Correct PIN
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect PIN. Try again.");
                }
            }
        }
        accounts = accountData.getAccounts();

        // ======= Load Properties Panel =======
        propertiesPanel = new PropertiesPanel();
        propertiesPanel.setOnAccountUpdated(updatedAccount -> {
            AccountStorage.saveData(accountData, pin);

            int selectedRow = accountTable.getSelectedRow();
            if (selectedRow != -1) {
                updateTableRow(selectedRow);
            }
        });

        // ======= Create Tabbed Pane =======
        JTabbedPane tabbedPane = new JTabbedPane();

        // ======= Create Tabbed Panels =======
        JPanel accountsPanel = new JPanel(new BorderLayout());
        JPanel settingsPanel = new JPanel(new BorderLayout());

        // ======= Top Panel (Buttons + PIN) =======
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Account");
        JButton removeButton = new JButton("Remove Account");
        JLabel pinLabel = new JLabel("Unhide PIN:");

        pinField = new JPasswordField(10);
        timerLabel = new JLabel(""); // Timer label (empty initially)

        pinField.addActionListener(e -> verifyPin()); // Verify on Enter

        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(pinLabel);
        topPanel.add(pinField);
        topPanel.add(timerLabel);

        hideButton = new JButton("Hide Passwords");
        hideButton.setVisible(false); // Initially hidden
        hideButton.addActionListener(e -> {
            if (hideTimer != null) {
                hideTimer.stop();
            }
            censorPasswords();
            timerLabel.setText("");
            hideButton.setVisible(false);
            JOptionPane.showMessageDialog(this, "Passwords hidden.");
        });

        topPanel.add(hideButton);

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

        // ======= Row selection listener =======
        accountTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = accountTable.getSelectedRow();
            if (selectedRow != -1) {
                Account selected = accounts.get(selectedRow);
                propertiesPanel.displayAccount(selected, passwordsVisible);
            } else {
                propertiesPanel.displayAccount(null, passwordsVisible);
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(accountTable);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);

        // ======= Right (Properties Panel) =======
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(propertiesPanel);

        // ======= Assemble Accounts Panel =======
        accountsPanel.add(topPanel, BorderLayout.NORTH);
        accountsPanel.add(splitPane, BorderLayout.CENTER);

        // ======= Add Tabs =======
        tabbedPane.addTab("Accounts", accountsPanel);
        tabbedPane.addTab("Settings", settingsPanel);

        // ======= Add TabbedPane to Main Panel =======
        add(tabbedPane, BorderLayout.CENTER);

        // ======= Load Table Data =======
        refreshTable();

        // ======= Button Actions =======
        addButton.addActionListener(e -> openAddAccountDialog());
        removeButton.addActionListener(e -> removeSelectedAccount());

    }

    /**
     * Refreshes table data with accounts (censored).
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{acc.getUsername(), "*****", acc.getNotes()});
        }
    }

    /**
     * Update the specific table row
     */
    private void updateTableRow(int rowIndex) {
        Account acc = accounts.get(rowIndex);
        String passwordDisplay = passwordsVisible ? acc.getPassword() : "*****";

        tableModel.setValueAt(acc.getUsername(), rowIndex, 0);
        tableModel.setValueAt(passwordDisplay, rowIndex, 1);
        tableModel.setValueAt(acc.getNotes(), rowIndex, 2);
    }

    /**
     * Opens the Add Account dialog.
     */
    private void openAddAccountDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddAccountDialog dialog = new AddAccountDialog(parentFrame);
        dialog.setVisible(true);

        if (dialog.isSubmitted()) {
            Account newAcc = dialog.getAccount();
            accounts.add(newAcc);
            accountData.setAccounts(accounts);
            AccountStorage.saveData(accountData, pin);            refreshTable();
        }
    }

    /**
     * Removes the selected account.
     */
    private void removeSelectedAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow != -1) {
            accounts.remove(selectedRow);
            accountData.setAccounts(accounts);
            AccountStorage.saveData(accountData, pin);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to remove.");
        }
    }

    /**
     * Reveals passwords in table and properties panel.
     */
    private void uncensorPasswords() {
        passwordsVisible = true;
        tableModel.setRowCount(0);
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{acc.getUsername(), acc.getPassword(), acc.getNotes()});
        }
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow != -1) {
            propertiesPanel.displayAccount(accounts.get(selectedRow), true);
        }
    }

    /**
     * Hides passwords and disables edit access in properties panel.
     */
    private void censorPasswords() {
        passwordsVisible = false;
        hideButton.setVisible(false);
        refreshTable();
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow != -1) {
            propertiesPanel.displayAccount(accounts.get(selectedRow), false);
        }
    }

    /**
     * Starts 3-minute timer to auto-hide passwords.
     */
    private void startHideTimer() {
        hideButton.setVisible(true);
        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.stop();
        }

        final int[] timeLeft = {180}; // 3 minutes in seconds

        hideTimer = new Timer(1000, e -> {
            timeLeft[0]--;
            timerLabel.setText("Visible: " + timeLeft[0] + "s");

            if (timeLeft[0] <= 0) {
                hideTimer.stop();
                censorPasswords();
                timerLabel.setText("");
                JOptionPane.showMessageDialog(this, "Passwords hidden again.");
            }
        });

        hideTimer.start();
    }

    /**
     * Verifies PIN input and unlocks passwords.
     */
    private void verifyPin() {
        String enteredPin = new String(pinField.getPassword());
        String storedPin = accountData.getPin();

        if (storedPin != null && enteredPin.equals(storedPin)) {
            uncensorPasswords();
            startHideTimer();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect PIN!");
        }
    }
}
