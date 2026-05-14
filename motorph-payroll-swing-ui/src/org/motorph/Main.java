package org.motorph;

import org.motorph.auth.LoginPage;
import org.motorph.auth.LoginViewModel;
import org.motorph.employees.ViewEmployeeInfoPage;
import org.motorph.employees.ViewEmployeeViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        var map = new HashMap<String, JPanel>();
        var loginViewModel = new LoginViewModel();
        var appViewModel = new AppViewModel();

        map.put(Routes.APP, new AppPage(appViewModel));
        map.put(Routes.LOGIN, new LoginPage(loginViewModel));
        map.put(Routes.VIEW_EMPLOYEE, new ViewEmployeeInfoPage(new ViewEmployeeViewModel()));

        var shellPanel = new Shell(map);
        Shell.Global = shellPanel;

        // Show the login page initially
        shellPanel.showPanel("login");

        var frame = new JFrame("Motorph Payroll");
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(shellPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
