package org.motorph;

import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;
import org.motorph.employees.LoginRepository;
import org.motorph.payroll.DateRange;
import org.motorph.payroll.PayrollService;
import org.motorph.payroll.ProcessRange;
import org.motorph.timesheet.Timesheet;
import org.motorph.timesheet.TimesheetRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/// Presentation and Business Rules to handle payroll operations
public class ConsolePayroll {
    private final EmployeeRepository employeeRepository;
    private final LoginRepository loginRepository;
    private final TimesheetRepository timesheetRepository;
    private final PayrollService payrollService;
    private final BufferedReader reader;

    /// Constructor to build ConsolePayroll with the default reader
    public ConsolePayroll(EmployeeRepository employeeRepository,
                          LoginRepository loginRepository,
                          TimesheetRepository timesheetRepository,
                          PayrollService payrollService){
        this(
                employeeRepository,
                loginRepository,
                timesheetRepository,
                payrollService,
                new BufferedReader(new InputStreamReader(System.in))
        );
    }

    public ConsolePayroll(EmployeeRepository employeeRepository,
                          LoginRepository loginRepository,
                          TimesheetRepository timesheetRepository,
                          PayrollService payrollService,
                          BufferedReader reader) {
        this.employeeRepository = employeeRepository;
        this.loginRepository = loginRepository;
        this.timesheetRepository = timesheetRepository;
        this.payrollService = payrollService;
        this.reader = reader;
    }

    /// Entry point to start the payroll operations
    public void start() {
        Employee currentEmployee = authenticateProcedure();

        if (currentEmployee.IsPayrollStaff()) {
            System.out.println("[MOTORPH] You are a payroll staff member.");
            if (questionAboutPayrollProcess()) {
                normalAccessProcedure(currentEmployee);
            } else {
                payrollAccessProcedure();
            }
        } else {
            normalAccessProcedure(currentEmployee);
        }
    }

    /// Asks the user to provide a username and password to authenticate
    private Employee authenticateProcedure() {
        System.out.println("[MOTORPH] Enter your username: ");
        var username = readLineOrThrow("[MOTORPH] No username entered");

        System.out.println("[MOTORPH] Enter your password: ");
        var password = readLineOrThrow("[MOTORPH] No password entered");

        var employee = this.loginRepository.getEmployeeByCredentials(username, password);
        if (employee == null) {
            System.out.println("[MOTORPH] Invalid username or password");
            throw new RuntimeException("[MOTORPH] Invalid username or password");
        }

        return employee;
    }

    /// Asks the user to either select normal access or access other employee's payroll data
    private boolean questionAboutPayrollProcess() {
        System.out.println("[MotorPH] Process your payroll or select an employee to process payroll.");
        System.out.println("1 - Your payroll, 2 - Other employees' payroll: ");
        var option = readLineOrThrow("[MotorPH] No option selected");
        switch (option) {
            case "1" -> {
                return true;
            }
            case "2" -> {
                return false;
            }
            default -> {
                System.out.println("[MotorPH] Invalid option selected");
                throw new RuntimeException("[MotorPH] Invalid option selected");
            }
        }
    }

    /// The base access for all users.
    /// Asks the user to select a month to process payroll for.
    private void normalAccessProcedure(Employee employee) {
        var availableMonths = timesheetRepository.getAllAvailableMonthsByEmployeeId(employee.EmployeeId);

        if (availableMonths.isEmpty()) {
            System.out.println("[MotorPH] No available months to process payroll");
            throw new RuntimeException("[MotorPH] No available months to process payroll");
        }

        var range = selectMonthlyOrCutoff();

        System.out.println("[MotorPH] Available months: all, " + availableMonths);
        var selectedMonth = readLineOrThrow("[MotorPH] No month selected");

        if (selectedMonth.equalsIgnoreCase("all")) {
            for (var month : availableMonths) {
                var timesheets = getTimesheetsByEmployeeAndRange(employee, range, month);
                generateAndPrintPayroll(employee, timesheets);
            }
        } else if (availableMonths.contains(selectedMonth.toUpperCase())) {
            var timesheets = getTimesheetsByEmployeeAndRange(employee, range, selectedMonth);
            System.out.println(
                    timesheets.stream()
                            .map(x -> x.StartTime.toString())
                            .collect(Collectors.joining("\n"))
            );
            generateAndPrintPayroll(employee, timesheets);
        } else {
            System.out.println("[MotorPH] Invalid month selected");
            throw new RuntimeException("[MotorPH] Invalid month selected");
        }
    }

    /// Access for the payroll team.
    /// Allows the payroll team to process payroll for all employees or a specific employee.
    private void payrollAccessProcedure() {
        // Ideally, this would be a paginated
        var employees = this.employeeRepository.getAllEmployees();
        System.out.println("[MotorPH] Please select an employee to process payroll."
                + " Please enter the employee ID or all to process all employees.");
        System.out.println("[MotorPH] Available employees: all\n " + employees.stream()
                .map(x -> x.getBasicDetails())
                .collect(Collectors.joining("\n")));

        var employeeId = readLineOrThrow("[MotorPH] No employee selected");

        if (employeeId.equalsIgnoreCase("all")) {
            var range = selectMonthlyOrCutoff();

            var months = this.timesheetRepository.getAllAvailableMonths();
            System.out.println("[MOTORPH] Select month to process payroll for all employees: " + months);
            var month = readLineOrThrow("[MotorPH] No month selected");

            if (months.contains(month.toUpperCase())) {
                for (var employee : employees) {
                    var timesheets = getTimesheetsByEmployeeAndRange(employee, range, month);
                    generateAndPrintPayroll(employee, timesheets);
                }
            } else {
                System.out.println("[MotorPH] Invalid month selected");
                throw new RuntimeException("[MotorPH] Invalid month selected");
            }
        } else if (employees.stream().anyMatch(x -> x.EmployeeId.equals(employeeId))) {
            var selectedEmployee = employees.stream()
                    .filter(x -> x.EmployeeId.equals(employeeId))
                    .findFirst()
                    .orElse(null);
            normalAccessProcedure(selectedEmployee);
        } else {
            System.out.println("[MotorPH] Invalid employee selected");
            throw new RuntimeException("[MotorPH] Invalid employee selected");
        }
    }

    /// Asks the user to select monthly or cutoff
    private ProcessRange selectMonthlyOrCutoff() {
        System.out.println("[MotorPH] Select monthly or cutoff {1 - month, 2 - first cutoff, 3 - second cutoff}: ");
        var selection = readLineOrThrow("[MotorPH] No selection made");
        if (selection.equalsIgnoreCase("1")) {
            return ProcessRange.MONTH;
        } else if (selection.equalsIgnoreCase("2")) {
            return ProcessRange.FIRST_CUTOFF;
        } else if (selection.equalsIgnoreCase("3")) {
            return ProcessRange.SECOND_CUTOFF;
        } else {
            System.out.println("[MotorPH] Invalid selection");
            throw new RuntimeException("[MotorPH] Invalid selection");
        }
    }

    /// Gets timesheets based on the month and range
    private List<Timesheet> getTimesheetsByEmployeeAndRange(Employee employee, ProcessRange range, String month) {
        var dateRange = DateRange.parseYearMonthInRange(month, range);
        return this.timesheetRepository
                .getAllTimesheetsByEmployeeIdAndDateRange(employee.EmployeeId, dateRange.start(), dateRange.end());
    }

    /// Generates and prints payroll for an employee
    private void generateAndPrintPayroll(Employee employee, List<Timesheet> timesheets) {
        var payroll = payrollService.generatePaySlip(employee, timesheets);
        System.out.println("Payroll for " + employee.getBasicDetails());
        System.out.println(payroll);
    }

    /// Reads a line from the console and throws an exception if the input is null
    private String readLineOrThrow(String errorMessage) {
        try {
            var value = reader.readLine();
            if (value == null) {
                System.out.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            return value;
        } catch (IOException e) {
            throw new RuntimeException("[MOTORPH] Failed to read input", e);
        }
    }
}

