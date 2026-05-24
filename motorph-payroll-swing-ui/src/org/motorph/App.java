package org.motorph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.motorph.auth.LoginForm;
import org.motorph.auth.LoginViewModel;
import org.motorph.employees.ManageEmployeeListForm;
import org.motorph.employees.ManageEmployeeListViewModel;
import org.motorph.employees.ViewEmployeeInfoForm;
import org.motorph.employees.ViewEmployeeViewModel;
import org.motorph.payroll.CalculatePayrollForm;
import org.motorph.payroll.CalculatePayrollViewModel;
import org.motorph.payroll.SelectTimesheetForm;
import org.motorph.payroll.SelectTimesheetViewModel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class App {
    private final JFrame frame;
    private HashMap<String, JPanel> routing;
    private Shell shell;
    private final Injector injector;
    public Runnable preRunAction = () -> {};

    public App() {
        this(new JFrame(), Guice.createInjector(new AppModule()));
    }

    public App(JFrame frame, Injector injector) {
        this.frame = frame;
        this.injector = injector;
        routing = new HashMap<String, JPanel>();
        routing.put(Routes.APP, new AppForm(injector.getInstance(AppViewModel.class)).getRootPanel());
        routing.put(Routes.LOGIN, new LoginForm(injector.getInstance(LoginViewModel.class)).getRootPanel());
        routing.put(Routes.VIEW_EMPLOYEE, new ViewEmployeeInfoForm(injector.getInstance(ViewEmployeeViewModel.class)).getRootPanel());
        routing.put(Routes.VIEW_PAYROLL, new SelectTimesheetForm(injector.getInstance(SelectTimesheetViewModel.class)).getRootPanel());
        routing.put(Routes.VIEW_PAYROLL__CALCULATOR, new CalculatePayrollForm(injector.getInstance(CalculatePayrollViewModel.class)).getRootPanel());
        routing.put(Routes.MANAGE_EMPLOYEES, new ManageEmployeeListForm(injector.getInstance(ManageEmployeeListViewModel.class)).getRootPanel());

        shell = new Shell(routing);
        Shell.Global = shell;

        // Show the login page initially
        shell.showPanel("login");
    }

    public void runApp() {
        preRunAction.run();
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(shell);
        frame.pack();
        frame.setVisible(true);
    }
}
