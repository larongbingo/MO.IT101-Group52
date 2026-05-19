package org.motorph.auth;

import org.motorph.listeners.TextFieldHandler;

import javax.swing.*;

public class LoginForm {
    private JTextField usernameTextField;
    private JPasswordField passwordTextField;
    private JButton loginButton;
    private JPanel rootPanel;
    private LoginViewModel viewModel;

    public LoginForm(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        LoginFormFormHelper.INSTANCE.initUI(this);

        // Bindings
        usernameTextField.getDocument().addDocumentListener(new TextFieldHandler(e ->{
            viewModel.username = e;
        }));
        passwordTextField.getDocument().addDocumentListener(new TextFieldHandler(e ->{
            viewModel.password = e;
        }));
        loginButton.addActionListener(e -> viewModel.onLogin());
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
}
