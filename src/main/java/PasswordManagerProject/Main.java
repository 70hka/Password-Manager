package PasswordManagerProject;

import PasswordManagerProject.ui.MainForm;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;


public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Password Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new MainForm());
            frame.setVisible(true);
        });
    }
}
