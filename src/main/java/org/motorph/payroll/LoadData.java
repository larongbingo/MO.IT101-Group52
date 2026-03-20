package org.motorph.payroll;

import de.siegmar.fastcsv.reader.CsvReader;
import org.motorph.Main;
import org.motorph.employees.Employee;
import org.motorph.employees.EmploymentStatus;
import org.motorph.employees.Login;
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

public class LoadData {
    public EmployeeLogin LoadEmployees() throws RuntimeException {
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
                    throw new RuntimeException("Issue caused by employee id = " + x.getField("Employee #"), e);
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

    public List<Timesheet> LoadTimesheets() throws RuntimeException {
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
                    LocalDateTime.parse(x.getField("Date") + " " + x.getField("Log In"),
                            DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm")),
                    LocalDateTime.parse(x.getField("Date") + " " + x.getField("Log Out"),
                            DateTimeFormatter.ofPattern("MM/dd/yyyy H:mm"))
            );
            timesheets.add(timesheet);
        });

        return timesheets;
    }
}

