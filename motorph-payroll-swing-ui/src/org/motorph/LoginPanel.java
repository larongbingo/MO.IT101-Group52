package org.motorph;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JPanel loginPanel;

    public LoginPanel() {
        this.usernameField = new JTextField();
        this.passwordField = new JPasswordField();
        this.loginButton = new JButton("Login");
        this.loginPanel = new JPanel();

        loginPanel.setLayout(new GridLayout(0, 1));

        loginPanel.setPreferredSize(new Dimension(300, 200));
        loginPanel.add(new JLabel("Username: "));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel()); // Spacer
        loginPanel.add(new JLabel("Password: "));
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Spacer
        loginPanel.add(loginButton);
        add(loginPanel);
    }
}
