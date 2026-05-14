package org.motorph.data;

import de.siegmar.fastcsv.reader.CsvReader;
import org.motorph.Main;
import org.motorph.core.MotorPhException;
import org.motorph.core.results.Failure;
import org.motorph.core.results.Success;
import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;
import org.motorph.employees.dto.NewEmployeeDto;
import org.motorph.employees.login.Login;
import org.motorph.employees.login.LoginRepository;
import org.motorph.timesheet.Timesheet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parses employee and attendance data from CSV streams
 * <p>
 * Remark: Pulled from ComProg1 Project
 * https://github.com/larongbingo/MO.IT101-Group55/tree/master
 */
public class LoadData {
    private final LoginRepository loginRepo;
    private final EmployeeRepository employeeRepo;
    public LoadData(LoginRepository loginRepo, EmployeeRepository employeeRepo) {
        this.loginRepo = loginRepo;
        this.employeeRepo = employeeRepo;
    }

    private List<Employee> loadEmployees(InputStream employeeStream) throws RuntimeException {
        List<Employee> employees = new ArrayList<>();

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
            });
        }
        catch (Exception e) {
            System.out.println("[MotorPH] Error parsing employees.csv \n" + e.getMessage());
        }

        // TODO: rebuild heirarchy from Immediate Supervisor column (not needed for now)

        return employees;
    }

    private List<Login> loadLogins(InputStream loginStream) throws RuntimeException {
        var logins = new ArrayList<Login>();

        var loginReader = new BufferedReader(new InputStreamReader(loginStream));
        var loginStringData = loginReader.lines().collect(Collectors.joining("\n"));

        var loginCsv = CsvReader.builder().ofNamedCsvRecord(loginStringData);

        loginCsv.stream().forEach(x -> {
            var login = new Login(
                    x.getField("Employee #"),
                    x.getField("Username"),
                    x.getField("Password")
            );
            logins.add(login);
        });

        return logins;
    }

    private List<Timesheet> loadTimesheets(InputStream attendanceStream) throws RuntimeException {
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

    public MotorPhData loadData() {
        var employeeStream = Main.class.getClassLoader().getResourceAsStream("employees.csv");
        if (employeeStream == null) {
            throw new RuntimeException("[MotorPH] Could not find employees.csv");
        }
        var attendanceStream = Main.class.getClassLoader().getResourceAsStream("attendance.csv");
        if (attendanceStream == null) {
            throw new RuntimeException("[MotorPH] Could not find attendance.csv");
        }
        var loginStream = Main.class.getClassLoader().getResourceAsStream("logins.csv");
        if (loginStream == null) {
            throw new RuntimeException("[MotorPH] Could not find logins.csv");
        }

        var employees = loadEmployees(employeeStream);
        var logins = loadLogins(loginStream);
        var timesheets = loadTimesheets(attendanceStream);

        return new MotorPhData(employees, logins, timesheets);
    }

    public void initReposWithData() {
        var motorPhData = loadData();
        motorPhData.logins().stream().forEach(login -> loginRepo.addLogin(login));
        motorPhData.employees().stream().forEach(employee -> employeeRepo.addEmployee(employee));
    }
}