package org.motorph;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Shell extends JPanel {
    private final CardLayout cardLayout;
    public static Shell Global;

    public Shell(Map<String, JPanel> panelMap) {
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);

        for (Map.Entry<String, JPanel> entry : panelMap.entrySet()) {
            add(entry.getValue(), entry.getKey());
        }

        // Show the first panel by default
        showPanel(panelMap.entrySet().iterator().next().getKey());

        setVisible(true);
    }

    public void showPanel(String panelName) {
        cardLayout.show(this, panelName);
    }

    public static void navigate(String panelName) {
        if (Global != null) {
            Global.showPanel(panelName);
        }
    }
}
