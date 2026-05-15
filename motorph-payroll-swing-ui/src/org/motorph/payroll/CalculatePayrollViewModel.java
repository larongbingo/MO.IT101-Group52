package org.motorph.payroll;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.auth.CurrentEmployeeLoggedIn;
import org.motorph.timesheet.TimesheetRepository;

public class CalculatePayrollViewModel {
    private final PayrollService payrollService;
    private final TimesheetRepository timesheetRepository;

    public CalculatePayrollViewModel(PayrollService payrollService, TimesheetRepository timesheetRepository) {
        this.payrollService = payrollService;
        this.timesheetRepository = timesheetRepository;
    }

    public Payroll calculatePayroll() {
        var employee = CurrentEmployeeLoggedIn.employee;
        var timesheet = timesheetRepository.getAllTimesheetsByEmployeeIdAndMonth(
                employee.EmployeeId,
                SelectedTimesheet.selectedTimesheetMonth.getFirst()
        );
        return payrollService.calculatePayroll(employee, timesheet);
    }

    public void goBack() {
        Shell.navigate(Routes.APP);
    }
}
