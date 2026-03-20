package org.motorph.payroll;

import de.siegmar.fastcsv.reader.CsvReader;
import org.motorph.Main;
import org.motorph.employees.*;
import org.motorph.payroll.repositories.ListEmployeeRepository;
import org.motorph.payroll.repositories.ListLoginRepository;
import org.motorph.payroll.repositories.ListTimesheetRepository;
import org.motorph.timesheet.Timesheet;
import org.motorph.timesheet.TimesheetRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/// Wrapper class for employee and login data
record EmployeeLogin(List<Employee> employees, List<Login> logins) {}

public class ConsolePayroll {
    private EmployeeRepository employeeRepository;
    private LoginRepository loginRepository;
    private TimesheetRepository timesheetRepository;
    private PayrollService payrollService;
    private Employee currentEmployee;

    public ConsolePayroll() {
        var employeeLogin = LoadEmployees();
        var timesheets = LoadTimesheets();

        this.employeeRepository = new ListEmployeeRepository(employeeLogin.employees());
        this.loginRepository = new ListLoginRepository(employeeLogin.logins(), this.employeeRepository);
        this.timesheetRepository = new ListTimesheetRepository(timesheets);
        this.payrollService = new PayrollService();
    }

    public void Start() {
        this.currentEmployee = AuthenticateProcedure();

        if (this.currentEmployee.IsPayrollStaff()) {
            System.out.println("[MOTORPH] You are a payroll staff member.");
            if (QuestionAboutPayrollProcess()) {
                NormalAccessProcedure(this.currentEmployee);
            } else {
                PayrollAccessProcedure();
            }
        } else {
            NormalAccessProcedure(this.currentEmployee);
        }
    }

    private Employee AuthenticateProcedure() {
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

    private boolean QuestionAboutPayrollProcess() {
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

    private void NormalAccessProcedure(Employee employee) {
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
                        .getAllTimesheetsByEmployeeIdAndAvailableMonthString(employee.EmployeeId, month);
                var payroll = payrollService.generatePaySlip(employee, timesheets);
                System.out.println(payroll);
            }
        }
        else if (availableMonths.contains(selectedMonth.toUpperCase())) {
            var timesheets = this.timesheetRepository
                    .getAllTimesheetsByEmployeeIdAndAvailableMonthString(employee.EmployeeId, selectedMonth);
            var payroll = payrollService.generatePaySlip(employee, timesheets);
            System.out.println(payroll);
        }
        else {
            System.out.println("[MotorPH] Invalid month selected");
            throw new RuntimeException("[MotorPH] Invalid month selected");
        }

    }

    private void PayrollAccessProcedure() {
        var employees = this.employeeRepository.getAllEmployees();
        System.out.println("[MotorPH] Please select an employee to process payroll. Please enter the employee ID or all to process all employees.");
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
                    var timesheets = this.timesheetRepository.getAllTimesheetsByEmployeeIdAndAvailableMonthString(employee.EmployeeId, month);
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
            for (var employee : employees) {
                var timesheets = this.timesheetRepository.getAllTimesheetsByEmployeeId(employee.EmployeeId);
                var payroll = payrollService.generatePaySlip(employee, timesheets);
                System.out.println("Payroll for " + employee.getBasicDetails());
                System.out.println(payroll);
            }
        }
        else {
            System.out.println("[MotorPH] Invalid employee selected");
            throw new RuntimeException("[MotorPH] Invalid employee selected");
        }
    }

    private EmployeeLogin LoadEmployees() throws RuntimeException {
        List<Employee> employees = new ArrayList<>();
        List<Login> logins = new ArrayList<>();

        var employeeStream = Main.class.getClassLoader().getResourceAsStream("employees.csv");
        if (employeeStream == null) {
            throw new RuntimeException("[MotorPH] Could not find employees.csv");
        }

        var usNumberFormat = NumberFormat.getInstance(Locale.US);

        var employeeReader = new BufferedReader(new InputStreamReader(employeeStream));
        var employeeStringData = employeeReader.lines().collect(Collectors.joining("\n"));

        var employeeCsv = CsvReader.builder().ofNamedCsvRecord(employeeStringData);
        try
        {
            employeeCsv.stream().forEach(x -> {
                Employee employee = null;
                try {
                    employee = new Employee(
                            x.getField("Employee #"),
                            x.getField("Last Name"),
                            x.getField("First Name"),
                            LocalDate.parse(x.getField("Birthday"), DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                            x.getField("Address"),
                            x.getField("Phone Number"),
                            x.getField("SSS #"),
                            x.getField("Philhealth #"),
                            x.getField("TIN #"),
                            x.getField("Pag-ibig #"),
                            x.getField("Status").equalsIgnoreCase("regular")
                                    ? EmploymentStatus.Regular
                                    : EmploymentStatus.Probationary,
                            x.getField("Position"),
                            usNumberFormat.parse(x.getField("Basic Salary")).doubleValue()
                    );
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                employees.add(employee);

                var login = new Login(
                        x.getField("Employee #"),
                        x.getField("Username"),
                        x.getField("Password")
                );
                logins.add(login);
            });
        }
        catch (Exception e) {
            System.out.println("[MotorPH] Error parsing employees.csv \n" + e.getMessage());
        }

        // TODO: rebuild heirarchy from Immediate Supervisor column (not needed for now)

        return new EmployeeLogin(employees, logins);
    }

    private List<Timesheet> LoadTimesheets() throws RuntimeException {
        List<Timesheet> timesheets = new ArrayList<>();

        var attendanceStream = Main.class.getClassLoader().getResourceAsStream("attendance.csv");
        if (attendanceStream == null) {
            throw new RuntimeException("[MotorPH] Could not find attendance.csv");
        }

        var attendanceReader = new BufferedReader(new InputStreamReader(attendanceStream));
        var attendanceStringData = attendanceReader.lines().collect(Collectors.joining("\n"));

        var attendanceCsv = CsvReader.builder().ofNamedCsvRecord(attendanceStringData);
        attendanceCsv.stream().forEach(x -> {
            var timesheet = new Timesheet(
                    x.getField("Employee #"),
                    LocalDateTime.parse(x.getField("Date") + " " + x.getField("Log In"), DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm")),
                    LocalDateTime.parse(x.getField("Date") + " " + x.getField("Log Out"), DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm"))
            );
            timesheets.add(timesheet);
        });

        return timesheets;
    }
}
