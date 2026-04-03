package org.motorph.payroll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.motorph.timesheet.Timesheet;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Unit Tests based on the example data shown in the link
///
/// https://docs.google.com/spreadsheets/u/2/d/1mWxdCuYCmTd8n3DrNVxIb912xT8dWFCsQTUc2owv2UQ/copy
class PayrollServiceTest {
    private final PayrollService sut = new PayrollService();

    @Test
    void countTotalWorkHours_returnsSumOfAllTimesheetHours() {
        // Arrange
        var timesheet = new Timesheet("1", LocalDateTime.of(2023, 1, 1, 0, 0), LocalDateTime.of(2023, 1, 1, 1, 0));

        // Act
        var result = sut.countTotalWorkHours(List.of(timesheet));

        // Assert
        assertEquals(1, result);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "23400.0, 513.4",
            "10000.0, 0.0"
    })
    void calculateTax_returnsCorrectTaxAmount(double income, double expectedTax) {
        // Arrange

        // Act
        var result = sut.calculateTax(income);

        // Assert
        assertEquals(expectedTax, result, 0.01);
    }

    @Test
    void calculatePhilHealthContribution_returnsCorrectContributionAmount() {
        // Arrange
        var income = 25000;

        // Act
        var result = sut.calculatePhilHealthContribution(income);

        // Assert
        assertEquals(375, result, 0.01);
    }

    @Test
    void calculatePagIbigContribution_returnsCorrectContributionAmount() {
        // Arrange
        var income = 25000;

        // Act
        var result = sut.calculatePagIbigContribution(income);

        // Assert
        assertEquals(100, result, 0.01);

    }

    @Test
    void calculateSSSContribution_returnsCorrectContributionAmount() {
        // Arrange
        var income = 25000;

        // Act
        var result = sut.calculateSSSContribution(income);

        // Assert
        assertEquals(1125, result, 0.01);
    }
}