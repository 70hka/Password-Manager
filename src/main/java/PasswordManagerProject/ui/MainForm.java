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

    private JLabel timerLabel;
    private Timer hideTimer;
    private JPasswordField pinField;
    private boolean passwordsVisible = false;
    private JButton hideButton;


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

        // ======= Check for pin =======
        SwingUtilities.invokeLater(() -> {
            if (!PasswordManagerProject.storage.PinStorage.pinExists()) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                CreatePinDialog dialog = new CreatePinDialog(parentFrame);
                dialog.setVisible(true);
            }
        });
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

    private void uncensorPasswords() {
        passwordsVisible = true;
        tableModel.setRowCount(0);
        for (PasswordManagerProject.model.Account acc : accounts) {
            tableModel.addRow(new Object[]{acc.getUsername(), acc.getPassword(), acc.getNotes()});
        }
    }

    private void censorPasswords() {
        passwordsVisible = false;
        hideButton.setVisible(false);
        refreshTable();
    }

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

    private void verifyPin() {
        String enteredPin = new String(pinField.getPassword());
        String storedPin = PasswordManagerProject.storage.PinStorage.loadPin();

        if (enteredPin.equals(storedPin)) {
            uncensorPasswords();
            startHideTimer();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect PIN!");
        }
    }
}
