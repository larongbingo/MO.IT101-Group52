package org.motorph;

import com.google.inject.Guice;
import org.motorph.auth.LoginPage;
import org.motorph.data.LoadData;
import org.motorph.employees.ViewEmployeeInfoPage;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new AppModule());
        injector.getInstance(LoadData.class).initReposWithData();

        var map = new HashMap<String, JPanel>();
        map.put(Routes.APP, injector.getInstance(AppPage.class));
        map.put(Routes.LOGIN, injector.getInstance(LoginPage.class));
        map.put(Routes.VIEW_EMPLOYEE, injector.getInstance(ViewEmployeeInfoPage.class));

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
