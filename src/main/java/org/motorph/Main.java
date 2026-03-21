package org.motorph;

import org.motorph.payroll.ConsolePayroll;
import org.motorph.payroll.LoadData;
import org.motorph.payroll.PayrollService;
import org.motorph.payroll.repositories.ListEmployeeRepository;
import org.motorph.payroll.repositories.ListLoginRepository;
import org.motorph.payroll.repositories.ListTimesheetRepository;

public class Main {
    /// Main composition for the ConsolePayroll app
    public static void main(String[] args) {
        // Load data
        var employeeStream = Main.class.getClassLoader().getResourceAsStream("employees.csv");
        if (employeeStream == null) {
            throw new RuntimeException("[MotorPH] Could not find employees.csv");
        }
        var attendanceStream = Main.class.getClassLoader().getResourceAsStream("attendance.csv");
        if (attendanceStream == null) {
            throw new RuntimeException("[MotorPH] Could not find attendance.csv");
        }

        // Parse data
        var dataLoader = new LoadData(employeeStream, attendanceStream);
        var employeeLogin = dataLoader.loadEmployees();
        var timesheets = dataLoader.loadTimesheets();

        // Initialize services
        var employeeRepository = new ListEmployeeRepository(employeeLogin.employees());
        var loginRepository = new ListLoginRepository(employeeLogin.logins(), employeeRepository);
        var timesheetRepository = new ListTimesheetRepository(timesheets);
        var payrollService = new PayrollService();

        // Start application
        new ConsolePayroll(employeeRepository, loginRepository, timesheetRepository, payrollService).start();
    }
}
