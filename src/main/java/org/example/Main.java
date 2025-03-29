package org.example;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;


public class Main {
    private JPanel panel1;
    private JButton button1;
    private JPasswordField passwordField;
    private JTextField userField;

    public static void main(String[] args) {
        FlatDarkLaf.setup();

        System.out.printf("Hello and welcome!");
    }

}
