package org.motorph.payroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.motorph.ConsolePayroll;
import org.motorph.core.MotorPhException;
import org.motorph.core.results.Result;
import org.motorph.employees.*;
import org.motorph.employees.login.Login;
import org.motorph.employees.login.LoginService;
import org.motorph.timesheet.TimesheetRepository;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ConsolePayrollTest {
    private EmployeeRepository employeeRepository;
    private LoginService loginService;
    private TimesheetRepository timesheetRepository;
    private PayrollService payrollService;
    private final Employee normalEmployee = new Employee(
            "E001",
            "Test",
            "Test",
            LocalDate.now(),
            "",
            "",
            "",
            "",
            "",
            "",
            EmploymentStatus.Regular,
            "Employee",
            10
    );
    private final Login normalLogin = new Login(normalEmployee.EmployeeId, "user", "pass");
    private final Employee payrollStaffEmployee = new Employee(
            "STAFF1",
            "Test",
            "Test",
            LocalDate.now(),
            "",
            "",
            "",
            "",
            "",
            "",
            EmploymentStatus.Regular,
            "Payroll",
            10
    );
    private final Login payrollStaffLogin = new Login(payrollStaffEmployee.EmployeeId, "staff_user", "staff_pass");

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        loginService = mock(LoginService.class);
        timesheetRepository = mock(TimesheetRepository.class);
        payrollService = mock(PayrollService.class);
    }

    @Test
    void start_whenCredentialsAreInvalid_throwsAndStops() {
        // Arrange
        when(loginService.login("user", "wrong"))
                .thenReturn(Result.failure(new MotorPhException("Invalid credentials")));

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginService,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("user\nwrong\n"))
        );
        assertThrows(RuntimeException.class, sut::start);

        // Assert
        verify(loginService).login("user", "wrong");
        verifyNoInteractions(employeeRepository, timesheetRepository, payrollService);
    }

    @Test
    void start_whenPayrollStaffAndChoosesNormalAccess_goesToNormalRoute() {
        // Arrange
        when(loginService.login(payrollStaffLogin.Username, payrollStaffLogin.Password))
                .thenReturn(Result.success(payrollStaffEmployee));
        when(timesheetRepository.getAllAvailableMonthsByEmployeeId(payrollStaffEmployee.EmployeeId))
                .thenReturn(List.of("JANUARY 2024"));
        when(timesheetRepository.getAllTimesheetsByEmployeeIdAndDateRange(anyString(), any(), any()))
                .thenReturn(List.of());
        when(payrollService.generatePaySlip(eq(payrollStaffEmployee), anyList()))
                .thenReturn("PAYSLIP");

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginService,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("staff_user\nstaff_pass\n1\n1\nJANUARY 2024\n"))
        );
        sut.start();

        // Assert
        verify(loginService).login(payrollStaffLogin.Username, payrollStaffLogin.Password);
        verify(timesheetRepository).getAllAvailableMonthsByEmployeeId(payrollStaffEmployee.EmployeeId);
        verify(timesheetRepository).getAllTimesheetsByEmployeeIdAndDateRange(eq(payrollStaffEmployee.EmployeeId), any(), any());
        verify(payrollService).generatePaySlip(eq(payrollStaffEmployee), anyList());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void start_whenPayrollStaffAndChoosesOtherEmployees_goesToPayrollAccessRoute() {
        // Arrange
        when(loginService.login(payrollStaffLogin.Username, payrollStaffLogin.Password))
                .thenReturn(Result.success(payrollStaffEmployee));
        when(employeeRepository.getAllEmployees()).thenReturn(List.of(normalEmployee, payrollStaffEmployee));
        when(timesheetRepository.getAllAvailableMonthsByEmployeeId(anyString()))
                .thenReturn(List.of("JANUARY 2024"));
        when(payrollService.generatePaySlip(any(Employee.class), anyList()))
                .thenReturn("PAYSLIP");

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginService,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("staff_user\nstaff_pass\n2\nE001\n1\nJANUARY 2024\n"))
        );
        sut.start();

        // Assert
        verify(loginService).login(payrollStaffLogin.Username, payrollStaffLogin.Password);
        verify(employeeRepository).getAllEmployees();
        verify(timesheetRepository).getAllAvailableMonthsByEmployeeId("E001");
        verify(payrollService).generatePaySlip(any(Employee.class), anyList());
    }

    @Test
    void start_whenNotPayrollStaff_skipsPayrollChoiceAndUsesNormalRoute() {
        // Arrange
        when(loginService.login(normalLogin.Username, normalLogin.Password))
                .thenReturn(Result.success(normalEmployee));
        when(timesheetRepository.getAllAvailableMonthsByEmployeeId("E001"))
                .thenReturn(List.of("JANUARY 2024"));
        when(timesheetRepository.getAllTimesheetsByEmployeeIdAndDateRange(anyString(), any(), any()))
                .thenReturn(List.of());
        when(payrollService.generatePaySlip(eq(normalEmployee), anyList()))
                .thenReturn("PAYSLIP");

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginService,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("user\npass\n1\nJANUARY 2024\n"))
        );
        sut.start();

        // Assert
        verify(loginService).login(normalLogin.Username, normalLogin.Password);
        verifyNoInteractions(employeeRepository);
        verify(timesheetRepository).getAllAvailableMonthsByEmployeeId("E001");
        verify(payrollService).generatePaySlip(eq(normalEmployee), anyList());
    }
}
