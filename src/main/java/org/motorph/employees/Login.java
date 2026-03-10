package org.motorph.employees;

public class Login {
    public Login(String employeeId, String username, String password) {
        this.EmployeeId = employeeId;
        this.Username = username;
        this.Password = password;
    }

    public final String EmployeeId;
    public String Username;
    public String Password;
}
