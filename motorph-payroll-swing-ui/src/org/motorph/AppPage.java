package org.motorph;

import javax.swing.*;

public class AppPage extends JPanel {
    private final JPanel appPanel;
    public AppPage() {
        this.appPanel = new JPanel();

        appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
        appPanel.add(new JLabel("Welcome to Motorph Payroll"));

        add(appPanel);
    }
}
