package org.motorph.employees.controls;

import org.motorph.employees.Employee;
import org.motorph.listeners.AncestorListenerHandler;

import javax.swing.*;
import java.awt.*;

public class EmployeeInfoPanel extends JPanel {
    public EmployeeInfoPanel(Employee employee) {
        setLayout(new GridLayout(0, 1));
        add(new JLabel("Employee ID: " + employee.EmployeeId));
        add(new JLabel("First Name: " + employee.FirstName));
        add(new JLabel("Last Name: " + employee.LastName));
        add(new JLabel("Position: " + employee.Position));
        add(new JLabel("Phone Number: " + employee.PhoneNumber));
        add(new JLabel("Address: " + employee.Address));
        add(new JLabel("Birthday: " + employee.Birthday));
        add(new JLabel("SSS Number: " + employee.SSSNumber));
        add(new JLabel("PhilHealth Number: " + employee.PhilHealthNumber));
        add(new JLabel("Pag-ibig Number: " + employee.PagibigMemberIdNumber));
        add(new JLabel("TIN Id: " + employee.TaxIdNumber));
        setVisible(true);

    }
}

