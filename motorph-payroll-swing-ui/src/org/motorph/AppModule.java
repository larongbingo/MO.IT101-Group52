package org.motorph;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.motorph.auth.ListLoginRepository;
import org.motorph.auth.LoginViewModel;
import org.motorph.auth.NoopStringHashing;
import org.motorph.data.LoadData;
import org.motorph.employees.*;
import org.motorph.employees.crypto.StringHashing;
import org.motorph.employees.login.LoginRepository;
import org.motorph.employees.login.LoginService;
import org.motorph.employees.login.LoginServiceImpl;
import org.motorph.payroll.*;
import org.motorph.timesheet.ListTimesheetRepository;
import org.motorph.timesheet.TimesheetRepository;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LoginRepository.class).to(ListLoginRepository.class).in(Singleton.class);
        bind(EmployeeRepository.class).to(ListEmployeeRepository.class).in(Singleton.class);
        bind(StringHashing.class).to(NoopStringHashing.class).in(Singleton.class);
        bind(AppViewModel.class).in(Singleton.class);
        bind(ViewEmployeeViewModel.class).in(Singleton.class);
        bind(TimesheetRepository.class).to(ListTimesheetRepository.class).in(Singleton.class);
        bind(PayrollService.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public LoadData provideLoadData(
            LoginRepository loginRepository, EmployeeRepository employeeRepository,
            TimesheetRepository timesheetRepository) {
        return new LoadData(loginRepository, employeeRepository, timesheetRepository);
    }

    @Provides
    @Singleton
    public LoginService provideLoginService(
            LoginRepository loginRepository,
            EmployeeRepository employeeRepository,
            StringHashing stringHashing) {
        return new LoginServiceImpl(loginRepository, stringHashing, employeeRepository);
    }

    @Provides
    @Singleton
    public ManageEmployeesService provideManageEmployeesService(EmployeeRepository employeeRepository) {
        return new ManageEmployeesServiceImpl(employeeRepository);
    }

    @Provides
    @Singleton
    public LoginViewModel provideLoginViewModel(LoginService loginService) {
        return new LoginViewModel(loginService);
    }

    @Provides
    public SelectTimesheetViewModel provideSelectTimesheetViewModel(TimesheetRepository timesheetRepository) {
        return new SelectTimesheetViewModel(timesheetRepository);
    }

    @Provides
    public CalculatePayrollViewModel provideCalculatePayrollViewModel(
            PayrollService payrollService, TimesheetRepository timesheetRepository) {
        return new CalculatePayrollViewModel(payrollService, timesheetRepository);
    }

    @Provides
    public ManageEmployeeListViewModel provideManageEmployeeListViewModel(
            EmployeeRepository employeeRepository, ManageEmployeesService manageEmployeesService) {
        return new ManageEmployeeListViewModel(employeeRepository, manageEmployeesService);
    }

    @Provides
    public ViewEmployeeInfoPage provideViewEmployeeInfoPage(ViewEmployeeViewModel viewModel) {
        return new ViewEmployeeInfoPage(viewModel);
    }

    @Provides
    public SelectTimesheetPage provideSelectTimesheetPage(SelectTimesheetViewModel viewModel) {
        return new SelectTimesheetPage(viewModel);
    }

    @Provides
    public CalculatePayrollPage provideCalculatePayrollPage(CalculatePayrollViewModel viewModel) {
        return new CalculatePayrollPage(viewModel);
    }

    @Provides
    public ManageEmployeeListPage provideManageEmployeeListPage(ManageEmployeeListViewModel viewModel) {
        return new ManageEmployeeListPage(viewModel);
    }
}
