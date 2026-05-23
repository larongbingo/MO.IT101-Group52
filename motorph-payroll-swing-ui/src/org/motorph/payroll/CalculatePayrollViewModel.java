package org.motorph.payroll;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.auth.CurrentEmployeeLoggedIn;
import org.motorph.timesheet.TimesheetRepository;

import java.util.ArrayList;
import java.util.List;

public class CalculatePayrollViewModel {
    private final PayrollService payrollService;
    private final TimesheetRepository timesheetRepository;

    public CalculatePayrollViewModel(PayrollService payrollService, TimesheetRepository timesheetRepository) {
        this.payrollService = payrollService;
        this.timesheetRepository = timesheetRepository;
    }

    public List<Payroll> calculatePayroll() {
        var employee = CurrentEmployeeLoggedIn.employee;
        var timesheets = SelectedTimesheet.selectedTimesheetMonth;
        var payrolls = new ArrayList<Payroll>();

        timesheets.stream()
            .map(timesheetMonth -> timesheetRepository.getAllTimesheetsByEmployeeIdAndMonth(employee.EmployeeId, timesheetMonth))
            .map(timesheetsForMonth -> payrollService.calculatePayroll(employee, timesheetsForMonth))
            .forEach(payroll -> payrolls.add(payroll));

        return payrolls;
    }

    public void goBack() {
        Shell.navigate(Routes.VIEW_PAYROLL);
    }

    public void goHome() {
        Shell.navigate(Routes.APP);
    }
}
