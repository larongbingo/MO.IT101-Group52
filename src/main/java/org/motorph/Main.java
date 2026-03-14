package org.motorph;

import de.siegmar.fastcsv.reader.CsvReader;
import org.motorph.employees.Employee;
import org.motorph.employees.EmploymentStatus;
import org.motorph.employees.Login;
import org.motorph.payroll.PayrollService;
import org.motorph.timesheet.Timesheet;

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

public class Main {
    private static List<Employee> employees = new ArrayList<>();
    private static List<Login> logins = new ArrayList<>();
    private static List<Timesheet> timesheets = new ArrayList<>();
    private static Employee currentEmployee;

    public static void main(String[] args) {
        LoadEmployees();

        Authenticate();

        System.out.println("[MotorPH] Welcome " + currentEmployee.FirstName + " " + currentEmployee.LastName);

        if (currentEmployee.IsPayrollStaff()) {

        } else {
            ProcessEmployeePayroll(currentEmployee);
        }

        // Needed Presentations
        // 1. Login
        // 2. Current Employee Display
        // 3.1. Current Employee only (default access)
        // 3.2. Process Payroll Selection (elevated access/staff)
            // 1. One Employee
            // 2. All Employees
    }

    // TODO(ComProg2): refactor to a separate class
    private static void LoadEmployees() {
        var employeeStream = Main.class.getClassLoader().getResourceAsStream("employees.csv");
        if (employeeStream == null) {
            System.out.println("[MotorPH] Could not find employees.csv");
            return;
        }

        var attendanceStream = Main.class.getClassLoader().getResourceAsStream("attendance.csv");
        if (attendanceStream == null) {
            System.out.println("[MotorPH] Could not find attendance.csv");
            return;
        }

        var employeeReader = new BufferedReader(new InputStreamReader(employeeStream));
        var attendanceReader = new BufferedReader(new InputStreamReader(attendanceStream));

        try {
            var usNumberFormat = NumberFormat.getInstance(Locale.US);

            var employeeStringData = employeeReader.lines().collect(Collectors.joining("\n"));
            var employeeCsv = CsvReader.builder().ofNamedCsvRecord(employeeStringData);
            employeeCsv.stream().forEach(x -> {
                Employee employee = null;
                // Try-Catch to satisfy usNumberFormat.parse throws
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
                            x.getField("Status").equalsIgnoreCase("regular") ? EmploymentStatus.Regular : EmploymentStatus.Probationary,
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

            // TODO: rebuild heirarchy from Immediate Supervisor column (not needed for now)

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

        } catch (Exception e) {
            System.out.println("[MotorPH] Error loading employees: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void Authenticate() {
        System.out.print("[MotorPH] Username: ");
        var username = System.console().readLine();
        System.out.print("[MotorPH] Password: ");
        var password = System.console().readPassword();

        if (username == null || password == null) {
            System.out.println("[MotorPH] Authentication failed: username or password cannot be null");
            throw new RuntimeException("Authentication failed: username or password cannot be null");
        }

        var login = logins.stream().filter(x -> x.Username.equals(username) && x.Password.equals(new String(password))).findFirst();
        if (login.isEmpty()) {
            System.out.println("[MotorPH] Authentication failed: invalid username or password");
            throw new RuntimeException("Authentication failed: invalid username or password");
        }

        var employee = employees.stream().filter(x -> x.EmployeeId.equals(login.get().EmployeeId)).findFirst();
        if (employee.isEmpty()) {
            System.out.println("[MotorPH] Authentication failed: invalid employee");
            throw new RuntimeException("Authentication failed: invalid employee");
        }

        System.out.println("[MotorPH] Authentication successful");
        currentEmployee = employee.get();
    }

    private static void ProcessEmployeePayroll(Employee employee) {
        var groupedTimesheet = timesheets.stream()
                .filter(x -> x.EmployeeId.equals(employee.EmployeeId))
                .collect(Collectors.groupingBy(x -> x.StartTime.getMonth() + " " + x.StartTime.getYear()));
        var availableMonths = groupedTimesheet.keySet();
        System.out.println(availableMonths);

        if (availableMonths.isEmpty()) {
            System.out.println("[MotorPH] No available months to process payroll");
            throw new RuntimeException("[MotorPH] No available months to process payroll");
        }

        // Select a month or process all available data
        System.out.println("[MotorPH] Available months: all, " + availableMonths);
        var month = System.console().readLine();
        if (month == null) {
            System.out.println("[MotorPH] No month selected");
            throw new RuntimeException("[MotorPH] No month selected");
        }
        if (month.equalsIgnoreCase("all")) {
            for (var monthTimesheet : groupedTimesheet.values()) {
                ProcessEmployeePayroll(employee, monthTimesheet);
            }
        }
        else {
            var monthTimesheet = groupedTimesheet.get(month.toUpperCase());
            if (monthTimesheet == null) {
                System.out.println("[MotorPH] No timesheet data for month " + month);
                throw new RuntimeException("[MotorPH] No timesheet data for month " + month);
            }
            ProcessEmployeePayroll(employee, monthTimesheet);
        }

    }

    private static void ProcessEmployeePayroll(Employee employee, List<Timesheet> timesheets)
    {
        var payrollService = new PayrollService();
        var totalHours = payrollService.countTotalWorkHours(timesheets);
        var grossPay = payrollService.calculateGrossPay(employee, timesheets);
        var sss = payrollService.calculateSSSContribution(grossPay);
        var philHealth = payrollService.calculatePhilHealthContribution(grossPay);
        var pagIbig = payrollService.calculatePagIbigContribution(grossPay);
        var totalDeductions = sss + philHealth + pagIbig;
        var taxableIncome = grossPay - totalDeductions;
        var tax = payrollService.calculateTax(taxableIncome);
        var netPay = taxableIncome - tax;

        System.out.println("[MotorPH] === " + timesheets.getFirst().StartTime.getMonth() + " " + timesheets.getFirst().StartTime.getYear());
        System.out.println("[MotorPH] Total Hours Worked: " + totalHours);
        System.out.println("[MotorPH] Gross Pay: Php " + grossPay);
        System.out.println("[MotorPH] SSS Contribution: Php " + sss);
        System.out.println("[MotorPH] PhilHealth Contribution: Php " + philHealth);
        System.out.println("[MotorPH] Pag-Ibig Contribution: Php " + pagIbig);
        System.out.println("[MotorPH] Total Deductions: Php " + totalDeductions);
        System.out.println("[MotorPH] Taxable Income: Php " + taxableIncome);
        System.out.println("[MotorPH] Tax: Php " + tax);
        System.out.println("[MotorPH] Net Pay: Php " + netPay);
    }
}
