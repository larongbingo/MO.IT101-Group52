package org.motorph;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        var loginPanel = new LoginPanel();
        var frame = new JFrame("Motorph Payroll");
        frame.setMinimumSize(new Dimension(1000, 800));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(loginPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
