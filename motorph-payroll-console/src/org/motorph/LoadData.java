package org.motorph;

import de.siegmar.fastcsv.reader.CsvReader;
import org.motorph.core.MotorPhException;
import org.motorph.core.results.Failure;
import org.motorph.core.results.Success;
import org.motorph.employees.Employee;
import org.motorph.employees.dto.NewEmployeeDto;
import org.motorph.employees.login.Login;
import org.motorph.payroll.EmployeeLogin;
import org.motorph.timesheet.Timesheet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/// Parses employee and attendance data from CSV streams
public class LoadData {
    private final InputStream employeeStream;
    private final InputStream attendanceStream;

    public LoadData(InputStream employeeStream, InputStream attendanceStream) {
        this.employeeStream = employeeStream;
        this.attendanceStream = attendanceStream;
    }

    /// Parses the employee and login data
    public EmployeeLogin loadEmployees() throws RuntimeException {
        List<Employee> employees = new ArrayList<>();
        List<Login> logins = new ArrayList<>();

        var employeeReader = new BufferedReader(new InputStreamReader(employeeStream));
        var employeeStringData = employeeReader.lines().collect(Collectors.joining("\n"));

        var employeeCsv = CsvReader.builder().ofNamedCsvRecord(employeeStringData);
        try
        {
            employeeCsv.stream().forEach(x -> {
                var employeeDto = new NewEmployeeDto(
                        x.getField("Employee #"),
                        x.getField("Last Name"),
                        x.getField("First Name"),
                        x.getField("Birthday"),
                        x.getField("Address"),
                        x.getField("Phone Number"),
                        x.getField("SSS #"),
                        x.getField("Philhealth #"),
                        x.getField("TIN #"),
                        x.getField("Pag-ibig #"),
                        x.getField("Status"),
                        x.getField("Position"),
                        x.getField("Basic Salary"),
                        null
                );

                var employeeResult = employeeDto.toEmployee();
                if (employeeResult instanceof Failure<Employee>(MotorPhException exception)) {
                    System.out.println("[MotorPH] Error parsing employee id " + x.getField("Employee #"));
                    System.out.println(exception.getMessage());
                    return;
                }

                var employee = ((Success<Employee>)employeeResult).value();
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

    /// Parses the attendance data
    public List<Timesheet> loadTimesheets() throws RuntimeException {
        List<Timesheet> timesheets = new ArrayList<>();

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

