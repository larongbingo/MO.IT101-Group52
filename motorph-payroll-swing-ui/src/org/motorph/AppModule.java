package org.motorph;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.motorph.auth.ListLoginRepository;
import org.motorph.auth.LoginPage;
import org.motorph.auth.LoginViewModel;
import org.motorph.auth.NoopStringHashing;
import org.motorph.data.LoadData;
import org.motorph.employees.*;
import org.motorph.employees.crypto.StringHashing;
import org.motorph.employees.login.LoginRepository;
import org.motorph.employees.login.LoginService;
import org.motorph.employees.login.LoginServiceImpl;
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
    @Singleton
    public LoginPage provideLoginPage(LoginViewModel viewModel) {
        return new LoginPage(viewModel);
    }

    @Provides
    @Singleton
    public AppPage provideAppPage(AppViewModel viewModel) {
        return new AppPage(viewModel);
    }

    @Provides
    @Singleton
    public ViewEmployeeInfoPage provideViewEmployeeInfoPage(ViewEmployeeViewModel viewModel) {
        return new ViewEmployeeInfoPage(viewModel);
    }

//    @Provides
//    @Singleton
//    public AppViewModel provideAppViewModel(LoginService loginService, ManageEmployeesService manageEmployeesService) {
//        return new AppViewModel();
//    }
}
