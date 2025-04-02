package PasswordManagerProject.ui;

import PasswordManagerProject.model.Account;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class PropertiesPanel extends JPanel {
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel websiteLabel;
    private JLabel notesLabel;

    private JButton editUsernameButton;
    private JButton editPasswordButton;
    private JButton editWebsiteButton;
    private JButton editNotesButton;

    private Account currentAccount;
    private boolean pinUnlocked = false;
    private Consumer<Account> onAccountUpdated;

    public PropertiesPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // ======= Initialize Labels =======
        usernameLabel = new JLabel();
        passwordLabel = new JLabel();
        websiteLabel = new JLabel();
        notesLabel = new JLabel();

        // ======= Initialize Buttons =======
        editUsernameButton = new JButton("Edit");
        editPasswordButton = new JButton("Edit");
        editWebsiteButton = new JButton("Edit");
        editNotesButton = new JButton("Edit");

        // ======= Button Actions =======
        editUsernameButton.addActionListener(e -> editProperty("Username"));
        editPasswordButton.addActionListener(e -> editProperty("Password"));
        editWebsiteButton.addActionListener(e -> editProperty("Website"));
        editNotesButton.addActionListener(e -> editProperty("Notes"));

        // ======= Add Components to Panel =======
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Username:"), c);
        c.gridx = 1;
        add(usernameLabel, c);
        c.gridx = 2;
        add(editUsernameButton, c);

        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Password:"), c);
        c.gridx = 1;
        add(passwordLabel, c);
        c.gridx = 2;
        add(editPasswordButton, c);

        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Website:"), c);
        c.gridx = 1;
        add(websiteLabel, c);
        c.gridx = 2;
        add(editWebsiteButton, c);

        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Notes:"), c);
        c.gridx = 1;
        add(notesLabel, c);
        c.gridx = 2;
        add(editNotesButton, c);

        styleButtons();
    }

    /**
     * Displays account info in the properties panel.
     */
    public void displayAccount(Account account, boolean pinUnlocked) {
        this.currentAccount = account;
        this.pinUnlocked = pinUnlocked;

        if (account != null) {
            usernameLabel.setText(account.getUsername());
            passwordLabel.setText(pinUnlocked ? account.getPassword() : "*****");
            websiteLabel.setText(account.getWebsite());
            notesLabel.setText(account.getNotes());
        } else {
            usernameLabel.setText("");
            passwordLabel.setText("");
            websiteLabel.setText("");
            notesLabel.setText("");
        }
    }

    /**
     * Edits a specific property of the selected account.
     */
    private void editProperty(String property) {
        if (currentAccount == null) return;

        String currentValue = "";
        switch (property) {
            case "Username": currentValue = currentAccount.getUsername(); break;
            case "Password":
                if (!pinUnlocked) {
                    JOptionPane.showMessageDialog(this, "Unlock PIN to edit password.");
                    return;
                }
                currentValue = currentAccount.getPassword();
                break;
            case "Website": currentValue = currentAccount.getWebsite(); break;
            case "Notes": currentValue = currentAccount.getNotes(); break;
        }

        String newValue = JOptionPane.showInputDialog(this, "Edit " + property + ":", currentValue);
        if (newValue != null && !newValue.isEmpty()) {
            switch (property) {
                case "Username": currentAccount.setUsername(newValue); break;
                case "Password": currentAccount.setPassword(newValue); break;
                case "Website": currentAccount.setWebsite(newValue); break;
                case "Notes": currentAccount.setNotes(newValue); break;
            }
            displayAccount(currentAccount, pinUnlocked);
            if (onAccountUpdated != null) {
                onAccountUpdated.accept(currentAccount);
            }
        }
    }

    /**
     * Sets a callback to trigger when account is updated.
     */
    public void setOnAccountUpdated(Consumer<Account> callback) {
        this.onAccountUpdated = callback;
    }

    /**
     * Set button sizes
     */
    private void styleButtons() {
        Dimension size = new Dimension(100,30);
        editUsernameButton.setPreferredSize(size);
        editPasswordButton.setPreferredSize(size);
        editNotesButton.setPreferredSize(size);
        editWebsiteButton.setPreferredSize(size);
    }
}
