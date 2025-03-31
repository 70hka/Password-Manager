package PasswordManagerProject.ui;

import PasswordManagerProject.model.Account;

import javax.swing.*;
import java.awt.*;

public class AddAccountDialog extends JDialog {
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField notesField;
    private JTextField websiteField;
    private boolean submitted = false;

    public AddAccountDialog(JFrame parent) {
        super(parent, "Add Account", true);
        setLayout(new GridLayout(5, 2, 5, 5));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JTextField();
        add(passwordField);

        add(new JLabel("Notes:"));
        notesField = new JTextField();
        add(notesField);

        add(new JLabel("Website:"));
        websiteField = new JTextField();
        add(websiteField);

        JButton submitButton = new JButton("Add");
        submitButton.addActionListener(e -> {
            submitted = true;
            setVisible(false);
        });
        add(submitButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> setVisible(false));
        add(cancelButton);

        setSize(400, 250);
        setLocationRelativeTo(parent);
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public Account getAccount() {
        return new Account(
                usernameField.getText(),
                passwordField.getText(),
                notesField.getText(),
                websiteField.getText()
        );
    }
}
