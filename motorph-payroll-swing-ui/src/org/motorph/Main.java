package org.motorph;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        var map = new HashMap<String, JPanel>();
        var loginViewModel = new LoginViewModel();

        map.put(Routes.APP, new AppPage());
        map.put(Routes.LOGIN, new LoginPage(loginViewModel));

        var shellPanel = new Shell(map);
        Shell.Global = shellPanel;

        // Show the login page initially
        shellPanel.showPanel("login");

        var frame = new JFrame("Motorph Payroll");
        frame.setMinimumSize(new Dimension(1000, 800));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(shellPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
