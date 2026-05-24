package org.motorph.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.motorph.App;
import org.motorph.AppModule;
import org.motorph.Routes;
import org.motorph.Shell;
import org.motorph.core.results.Failure;
import org.motorph.data.LoadData;

import javax.swing.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class LoginE2ETest {
    private Injector injector;
    private App app;
    private LoginViewModel loginViewModel;

    @BeforeEach
    void setUp() {
        // We use the real AppModule but we might need to be careful with static state
        injector = Guice.createInjector(new AppModule());
        // Initialize data
        injector.getInstance(LoadData.class).initReposWithData();
        
        // Reset static state
        CurrentEmployeeLoggedIn.employee = null;
        Shell.Global = null;

        // Initialize App (without showing the frame to avoid flickering/headless issues)
        app = new App(new JFrame(), injector);
    }

    @Test
    void testSuccessfulLogin() {
        loginViewModel = injector.getInstance(LoginViewModel.class);
        loginViewModel.username = "staff1";
        loginViewModel.password = "staff1";

        var result = loginViewModel.onLogin();

        assertTrue(result.isSuccess(), "Login should be successful");
        assertNotNull(CurrentEmployeeLoggedIn.employee, "Employee should be logged in");
        assertEquals("10001", CurrentEmployeeLoggedIn.employee.EmployeeId);
    }

    @Test
    void testFailedLoginInvalidCredentials() {
        loginViewModel = injector.getInstance(LoginViewModel.class);
        loginViewModel.username = "staff1";
        loginViewModel.password = "wrongpassword";

        var result = loginViewModel.onLogin();

        assertFalse(result.isSuccess(), "Login should fail with wrong password");
        assertNull(CurrentEmployeeLoggedIn.employee, "Employee should not be logged in");
    }

    @Test
    void testFailedLoginEmptyUsername() {
        loginViewModel = injector.getInstance(LoginViewModel.class);
        loginViewModel.username = "";
        loginViewModel.password = "staff1";

        var result = loginViewModel.onLogin();

        assertFalse(result.isSuccess(), "Login should fail with empty username");
        assertEquals("Username cannot be empty", ((Failure<?>)result).exception().getMessage());
    }

    @Test
    void testFailedLoginEmptyPassword() {
        loginViewModel = injector.getInstance(LoginViewModel.class);
        loginViewModel.username = "staff1";
        loginViewModel.password = "";

        var result = loginViewModel.onLogin();

        assertFalse(result.isSuccess(), "Login should fail with empty password");
        assertEquals("Password cannot be empty", ((Failure<?>)result).exception().getMessage());
    }
}
