package org.motorph;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class LoginPage extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JPanel loginPanel;

    public LoginPage(LoginViewModel viewModel) {
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

        usernameField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            viewModel.username = e;
        }));

        passwordField.getDocument().addDocumentListener(new TextFieldHandler(e -> {
            viewModel.password = e;
        }));

        loginButton.addActionListener(e -> {
            viewModel.onLogin();
        });
    }
}

class TextFieldHandler implements DocumentListener {
    private final Consumer<String> action;
    public TextFieldHandler(Consumer<String> action) {
        this.action = action;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            action.accept(e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            action.accept(e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
