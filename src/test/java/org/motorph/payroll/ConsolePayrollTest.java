package org.motorph.payroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.motorph.employees.Employee;
import org.motorph.employees.EmployeeRepository;
import org.motorph.employees.EmploymentStatus;
import org.motorph.employees.LoginRepository;
import org.motorph.timesheet.TimesheetRepository;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ConsolePayrollTest {
    private EmployeeRepository employeeRepository;
    private LoginRepository loginRepository;
    private TimesheetRepository timesheetRepository;
    private PayrollService payrollService;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        loginRepository = mock(LoginRepository.class);
        timesheetRepository = mock(TimesheetRepository.class);
        payrollService = mock(PayrollService.class);
    }

    
    @Test
    void start_whenCredentialsAreInvalid_throwsAndStops() {
        // Arrange
        when(loginRepository.getEmployeeByCredentials("user", "wrong"))
                .thenReturn(null);

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginRepository,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("user\nwrong\n"))
        );
        assertThrows(RuntimeException.class, sut::start);

        // Assert
        verify(loginRepository).getEmployeeByCredentials("user", "wrong");
        verifyNoInteractions(employeeRepository, timesheetRepository, payrollService);
    }

    @Test
    void start_whenPayrollStaffAndChoosesNormalAccess_goesToNormalRoute() {
        // Arrange
        var employee = new Employee(
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
                "Payroll", // required to return true on IsPayrollStaff(),
                10
        );
        when(loginRepository.getEmployeeByCredentials("user", "pass")).thenReturn(employee);
        when(timesheetRepository.getAllAvailableMonthsByEmployeeId("E001"))
                .thenReturn(List.of("JANUARY 2024"));
        when(timesheetRepository.getAllTimesheetsByEmployeeIdAndDateRange(anyString(), any(), any()))
                .thenReturn(List.of());
        when(payrollService.generatePaySlip(eq(employee), anyList()))
                .thenReturn("PAYSLIP");

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginRepository,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("user\npass\n1\n1\nJANUARY 2024\n"))
        );
        sut.start();

        // Assert
        verify(loginRepository).getEmployeeByCredentials("user", "pass");
        verify(timesheetRepository).getAllAvailableMonthsByEmployeeId("E001");
        verify(timesheetRepository).getAllTimesheetsByEmployeeIdAndDateRange(eq("E001"), any(), any());
        verify(payrollService).generatePaySlip(eq(employee), anyList());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void start_whenPayrollStaffAndChoosesOtherEmployees_goesToPayrollAccessRoute() {
        // Arrange
        var staffEmployee = new Employee(
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
                "Payroll", // required to return true on IsPayrollStaff(),
                10
        );
        when(loginRepository.getEmployeeByCredentials("user", "pass")).thenReturn(staffEmployee);

        var targetEmployee = new Employee(
                "E001",
                "John",
                "Doe",
                LocalDate.now(),
                "",
                "",
                "",
                "",
                "",
                "",
                EmploymentStatus.Regular,
                "Payroll", // required to return true on IsPayrollStaff(),
                10
        );
        when(employeeRepository.getAllEmployees()).thenReturn(List.of(targetEmployee));
        when(timesheetRepository.getAllAvailableMonthsByEmployeeId(anyString()))
                .thenReturn(List.of("JANUARY 2024"));
        when(payrollService.generatePaySlip(any(Employee.class), anyList()))
                .thenReturn("PAYSLIP");

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginRepository,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("user\npass\n2\nE001\n1\nJANUARY 2024\n"))
        );
        sut.start();

        // Assert
        verify(loginRepository).getEmployeeByCredentials("user", "pass");
        verify(employeeRepository).getAllEmployees();
        verify(timesheetRepository).getAllAvailableMonthsByEmployeeId("E001");
        verify(payrollService).generatePaySlip(any(Employee.class), anyList());
    }

    @Test
    void start_whenNotPayrollStaff_skipsPayrollChoiceAndUsesNormalRoute() {
        // Arrange
        var employee = new Employee(
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
                "",
                10
        );
        when(loginRepository.getEmployeeByCredentials("user", "pass")).thenReturn(employee);
        when(timesheetRepository.getAllAvailableMonthsByEmployeeId("E001"))
                .thenReturn(List.of("JANUARY 2024"));
        when(timesheetRepository.getAllTimesheetsByEmployeeIdAndDateRange(anyString(), any(), any()))
                .thenReturn(List.of());
        when(payrollService.generatePaySlip(eq(employee), anyList()))
                .thenReturn("PAYSLIP");

        // Act
        var sut = new ConsolePayroll(
                employeeRepository,
                loginRepository,
                timesheetRepository,
                payrollService,
                new BufferedReader(new StringReader("user\npass\n1\nJANUARY 2024\n"))
        );
        sut.start();

        // Assert
        verify(loginRepository).getEmployeeByCredentials("user", "pass");
        verifyNoInteractions(employeeRepository);
        verify(timesheetRepository).getAllAvailableMonthsByEmployeeId("E001");
        verify(payrollService).generatePaySlip(eq(employee), anyList());
    }
}
