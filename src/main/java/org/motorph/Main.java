package org.motorph;

public class Main {
    public static void main(String[] args) {
        double hoursWorked = 45.0;
        double hoursPerDay = 8.0;
        double paySchedule = 5 * 2; // 2 Working Weeks

        double totalDaysWorked = hoursWorked / hoursPerDay;
        double hoursRemainingFor2Weeks = (hoursWorked * hoursPerDay) - (hoursPerDay * paySchedule);

        System.out.println("Total Days Worked: " + totalDaysWorked);
        System.out.println("Hours Remaining for 2 Weeks: " + hoursRemainingFor2Weeks);
    }
}

