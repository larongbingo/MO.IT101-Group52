package org.motorph.payroll;

import org.motorph.employees.*;
import org.motorph.timesheet.TimesheetRepository;

import java.util.stream.Collectors;

public class ConsolePayroll {
    private final EmployeeRepository employeeRepository;
    private final LoginRepository loginRepository;
    private final TimesheetRepository timesheetRepository;
    private final PayrollService payrollService;

    public ConsolePayroll(EmployeeRepository employeeRepository,
                          LoginRepository loginRepository,
                          TimesheetRepository timesheetRepository,
                          PayrollService payrollService) {

        this.employeeRepository = employeeRepository;
        this.loginRepository = loginRepository;
        this.timesheetRepository = timesheetRepository;
        this.payrollService = payrollService;
    }

    public void Start() {
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

    private Employee authenticateProcedure() {
        System.out.print("[MOTORPH] Enter your username: ");
        var username = System.console().readLine();
        System.out.print("[MOTORPH] Enter your password: ");
        var password = System.console().readPassword();

        var employee = this.loginRepository.getEmployeeByCredentials(username, new String(password));
        if (employee == null) {
            System.out.println("[MOTORPH] Invalid username or password");
            throw new RuntimeException("[MOTORPH] Invalid username or password");
        }

        return employee;
    }

    private boolean questionAboutPayrollProcess() {
        System.out.println("[MotorPH] Process your payroll or select an employee to process payroll.");
        System.out.print("1 - Your payroll, 2 - Other employees' payroll: ");
        var option = System.console().readLine();
        switch (option) {
            case null -> {
                System.out.println("[MotorPH] No option selected");
                throw new RuntimeException("[MotorPH] No option selected");
            }
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

    private void normalAccessProcedure(Employee employee) {
        var availableMonths = timesheetRepository.getAllAvailableMonthsByEmployeeId(employee.EmployeeId);

        if (availableMonths.isEmpty()) {
            System.out.println("[MotorPH] No available months to process payroll");
            throw new RuntimeException("[MotorPH] No available months to process payroll");
        }

        System.out.println("[MotorPH] Available months: all, " + availableMonths);
        var selectedMonth = System.console().readLine();
        if (selectedMonth == null) {
            System.out.println("[MotorPH] No month selected");
            throw new RuntimeException("[MotorPH] No month selected");
        }
        else if (selectedMonth.equalsIgnoreCase("all")) {
            for (var month : availableMonths) {
                var timesheets = this.timesheetRepository
                        .getAllTimesheetsByEmployeeIdAndMonth(employee.EmployeeId, month);
                var payroll = payrollService.generatePaySlip(employee, timesheets);
                System.out.println(payroll);
            }
        }
        else if (availableMonths.contains(selectedMonth.toUpperCase())) {
            var timesheets = this.timesheetRepository
                    .getAllTimesheetsByEmployeeIdAndMonth(employee.EmployeeId, selectedMonth);
            var payroll = payrollService.generatePaySlip(employee, timesheets);
            System.out.println(payroll);
        }
        else {
            System.out.println("[MotorPH] Invalid month selected");
            throw new RuntimeException("[MotorPH] Invalid month selected");
        }

    }

    private void payrollAccessProcedure() {
        var employees = this.employeeRepository.getAllEmployees();
        System.out.println("[MotorPH] Please select an employee to process payroll."
                + " Please enter the employee ID or all to process all employees.");
        System.out.println("[MotorPH] Available employees: all\n " + employees.stream()
                .map(x -> x.getBasicDetails())
                .collect(Collectors.joining("\n")));
        var employeeId = System.console().readLine();
        if (employeeId == null) {
            System.out.println("[MotorPH] No employee selected");
            throw new RuntimeException("[MotorPH] No employee selected");
        }
        else if (employeeId.equalsIgnoreCase("all")) {
            var months = this.timesheetRepository.getAllAvailableMonths();
            System.out.println("[MOTORPH] Select month to process payroll for all employees: " + months);

            var month = System.console().readLine();
            if (month == null) {
                System.out.println("[MotorPH] No month selected");
                throw new RuntimeException("[MotorPH] No month selected");
            }
            else if (months.contains(month.toUpperCase())) {
                for (var employee : employees) {
                    var timesheets = this.timesheetRepository
                            .getAllTimesheetsByEmployeeIdAndMonth(employee.EmployeeId, month);
                    var payroll = payrollService.generatePaySlip(employee, timesheets);
                    System.out.println("Payroll for " + employee.getBasicDetails());
                    System.out.println(payroll);
                }
            }
            else {
                System.out.println("[MotorPH] Invalid month selected");
                throw new RuntimeException("[MotorPH] Invalid month selected");
            }
        }
        else if (employees.stream().anyMatch(x -> x.EmployeeId.equals(employeeId))) {
            var selectedEmployee = employees.stream()
                    .filter(x -> x.EmployeeId.equals(employeeId))
                    .findFirst()
                    .orElse(null);
            var timesheets = this.timesheetRepository.getAllTimesheetsByEmployeeId(selectedEmployee.EmployeeId);
            var payroll = payrollService.generatePaySlip(selectedEmployee, timesheets);
            System.out.println("Payroll for " + selectedEmployee.getBasicDetails());
            System.out.println(payroll);
        }
        else {
            System.out.println("[MotorPH] Invalid employee selected");
            throw new RuntimeException("[MotorPH] Invalid employee selected");
        }
    }
}
