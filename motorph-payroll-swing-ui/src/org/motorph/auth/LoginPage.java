package org.motorph.auth;

import org.motorph.core.results.Failure;
import org.motorph.employees.Employee;
import org.motorph.listeners.TextFieldHandler;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JPanel {
    private final LoginViewModel viewModel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorMessageLabel;
    private final JButton loginButton;
    private final JPanel loginPanel;

    public LoginPage(LoginViewModel viewModel) {
        this.usernameField = new JTextField();
        this.passwordField = new JPasswordField();
        this.loginButton = new JButton("Login");
        this.loginPanel = new JPanel();
        this.errorMessageLabel = new JLabel();
        this.viewModel = viewModel;

        // Layout
        loginPanel.setLayout(new GridLayout(0, 1));
        loginPanel.setPreferredSize(new Dimension(300, 200));
        loginPanel.add(new JLabel("Username: "));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel()); // Spacer
        loginPanel.add(new JLabel("Password: "));
        loginPanel.add(passwordField);
        loginPanel.add(errorMessageLabel);
        loginPanel.add(new JLabel()); // Spacer
        loginPanel.add(loginButton);
        add(loginPanel);

        // Bindings
        usernameField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            viewModel.username = e;
        }));

        passwordField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            viewModel.password = e;
        }));

        loginButton.addActionListener(e -> {
            var result = viewModel.onLogin();
            if (result instanceof Failure<Employee> failure) {
                errorMessageLabel.setText(failure.exception().getMessage());
            }
        });
    }
}
