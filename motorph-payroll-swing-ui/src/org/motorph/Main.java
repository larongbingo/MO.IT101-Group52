package org.motorph;

import com.google.inject.Guice;
import org.motorph.auth.LoginForm;
import org.motorph.auth.LoginViewModel;
import org.motorph.data.LoadData;
import org.motorph.employees.ManageEmployeeListPage;
import org.motorph.employees.ViewEmployeeInfoForm;
import org.motorph.employees.ViewEmployeeViewModel;
import org.motorph.payroll.CalculatePayrollPage;
import org.motorph.payroll.SelectTimesheetForm;
import org.motorph.payroll.SelectTimesheetViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new AppModule());
        injector.getInstance(LoadData.class).initReposWithData();

        var map = new HashMap<String, JPanel>();
        map.put(Routes.APP, new AppForm(injector.getInstance(AppViewModel.class)).getRootPanel());
        map.put(Routes.LOGIN, new LoginForm(injector.getInstance(LoginViewModel.class)).getRootPanel());
        map.put(Routes.VIEW_EMPLOYEE, new ViewEmployeeInfoForm(injector.getInstance(ViewEmployeeViewModel.class)).getRootPanel());
        map.put(Routes.VIEW_PAYROLL, new SelectTimesheetForm(injector.getInstance(SelectTimesheetViewModel.class)).getRootPanel());
        map.put(Routes.VIEW_PAYROLL__CALCULATOR, injector.getInstance(CalculatePayrollPage.class));
        map.put(Routes.MANAGE_EMPLOYEES, injector.getInstance(ManageEmployeeListPage.class));

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
