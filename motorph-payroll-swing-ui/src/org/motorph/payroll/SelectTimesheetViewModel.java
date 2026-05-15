package org.motorph.payroll;

import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.auth.CurrentEmployeeLoggedIn;
import org.motorph.timesheet.TimesheetRepository;

import java.util.List;

public class SelectTimesheetViewModel {
    public String selectedTimesheetMonth;
    public boolean isAllSelected;
    private final TimesheetRepository timesheetRepository;

    public SelectTimesheetViewModel(TimesheetRepository timesheetRepository) {
        this.timesheetRepository = timesheetRepository;
    }

    public List<String> getAllAvailableTimesheets() {
        return timesheetRepository.getAllAvailableMonthsByEmployeeId(
                CurrentEmployeeLoggedIn.employee.EmployeeId
        );
    }

    public void Submit() {
        if (isAllSelected) {
            SelectedTimesheet.selectedTimesheetMonth = getAllAvailableTimesheets();
        } else {
            SelectedTimesheet.selectedTimesheetMonth = List.of(selectedTimesheetMonth);
        }

        Shell.navigate(Routes.VIEW_PAYROLL__CALCULATOR);
    }

    public void GoBack() {
        Shell.navigate(Routes.APP);
    }
}
