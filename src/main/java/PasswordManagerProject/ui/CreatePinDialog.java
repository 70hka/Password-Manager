package PasswordManagerProject.ui;

import PasswordManagerProject.storage.PinStorage;

import javax.swing.*;
import java.awt.*;

public class CreatePinDialog extends JDialog {
    public CreatePinDialog(JFrame parent) {
        super(parent, "Create PIN", true);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("No PIN found. Please create one:");
        JPasswordField pinField = new JPasswordField(10);
        JButton submit = new JButton("Save PIN");

        submit.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            if (!pin.isEmpty()) {
                PinStorage.savePin(pin);
                JOptionPane.showMessageDialog(this, "PIN saved.");
                setVisible(false);
            }
        });

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(label);
        panel.add(pinField);
        panel.add(submit);

        add(panel, BorderLayout.CENTER);
        setSize(300, 120);
        setLocationRelativeTo(parent);
    }
}
