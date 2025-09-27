package com.example.trend;

import javax.swing.*;
import java.awt.*;

public class TrendAppFrame extends JFrame {

    public TrendAppFrame() {
        super("Оценка стабильности трендов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        add(new JLabel("UI success", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
