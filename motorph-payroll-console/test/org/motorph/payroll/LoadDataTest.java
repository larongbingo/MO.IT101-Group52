package org.motorph.payroll;

import org.junit.jupiter.api.Test;
import org.motorph.LoadData;
import org.motorph.employees.EmploymentStatus;
import org.motorph.timesheet.Timesheet;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoadDataTest {

    @Test
    void loadEmployees_whenCsvIsValid_parsesEmployeesAndLogins() {
        // Arrange
        String employeesCsv = String.join("\n",
                "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Basic Salary,Username,Password",
                "E001,Doe,John,01/15/1990,123 Main St,09171234567,SSS-1,PH-1,TIN-1,PAG-1,Regular,Software Engineer,50000.50,jdoe,secret",
                "E002,Smith,Jane,02/20/1992,456 Oak St,09179876543,SSS-2,PH-2,TIN-2,PAG-2,Probationary,QA Analyst,32000.00,jsmith,pass123"
        );

        var sut = new LoadData(
                new ByteArrayInputStream(employeesCsv.getBytes(StandardCharsets.UTF_8)),
                new ByteArrayInputStream(new byte[0])
        );

        // Act
        EmployeeLogin result = sut.loadEmployees();

        // Assert
        assertEquals(2, result.employees().size());
        assertEquals(2, result.logins().size());

        var employee1 = result.employees().get(0);
        assertEquals("E001", employee1.EmployeeId);
        assertEquals("Doe", employee1.LastName);
        assertEquals("John", employee1.FirstName);
        assertEquals(LocalDate.of(1990, 1, 15), employee1.Birthday);
        assertEquals(EmploymentStatus.Regular, employee1.EmploymentStatus);
        assertEquals("Software Engineer", employee1.Position);
        assertEquals(50000.50, employee1.BasicSalary, 0.0001);

        var employee2 = result.employees().get(1);
        assertEquals("E002", employee2.EmployeeId);
        assertEquals(EmploymentStatus.Probationary, employee2.EmploymentStatus);

        var login1 = result.logins().get(0);
        assertEquals("E001", login1.EmployeeId);
        assertEquals("jdoe", login1.Username);
        assertEquals("secret", login1.Password);
    }

    @Test
    void loadTimesheets_whenCsvIsValid_parsesTimesheets() {
        // Arrange
        String attendanceCsv = String.join("\n",
                "Employee #,Date,Log In,Log Out",
                "E001,01/15/2024,8:00,17:00",
                "E002,01/16/2024,9:15,18:30"
        );

        var sut = new LoadData(
                new ByteArrayInputStream(new byte[0]),
                new ByteArrayInputStream(attendanceCsv.getBytes(StandardCharsets.UTF_8))
        );

        // Act
        List<Timesheet> result = sut.loadTimesheets();

        // Assert
        assertEquals(2, result.size());

        var ts1 = result.get(0);
        assertEquals("E001", ts1.EmployeeId);
        assertEquals(LocalDateTime.of(2024, 1, 15, 8, 0), ts1.StartTime);
        assertEquals(LocalDateTime.of(2024, 1, 15, 17, 0), ts1.EndTime);

        var ts2 = result.get(1);
        assertEquals("E002", ts2.EmployeeId);
        assertEquals(LocalDateTime.of(2024, 1, 16, 9, 15), ts2.StartTime);
        assertEquals(LocalDateTime.of(2024, 1, 16, 18, 30), ts2.EndTime);
    }

    @Test
    void loadEmployees_whenSalaryIsInvalid_returnsEmptyLists() {
        // Arrange
        String employeesCsv = String.join("\n",
                "Employee #,Last Name,First Name,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,Status,Position,Basic Salary,Username,Password",
                "E001,Doe,John,01/15/1990,123 Main St,09171234567,SSS-1,PH-1,TIN-1,PAG-1,Regular,Software Engineer,not-a-number,jdoe,secret"
        );

        var sut = new LoadData(
                new ByteArrayInputStream(employeesCsv.getBytes(StandardCharsets.UTF_8)),
                new ByteArrayInputStream(new byte[0])
        );

        // Act
        EmployeeLogin result = sut.loadEmployees();

        // Assert
        assertTrue(result.employees().isEmpty());
        assertTrue(result.logins().isEmpty());
    }
}
