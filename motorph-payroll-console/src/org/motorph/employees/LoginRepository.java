package org.motorph.employees;

/// Common queries for employee login data
public interface LoginRepository {
    /// Fetches an employee by their username and password
    Employee getEmployeeByCredentials(String username, String password);
}
