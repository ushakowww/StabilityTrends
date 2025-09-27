package com.example.trend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TrendAppFrame extends JFrame {

    private final JTextField fileField = new JTextField();
    private final JSpinner thresholdSpinner = new JSpinner(new SpinnerNumberModel(3.0, 0.0, 10000.0, 0.1));
    private final JCheckBox chkShowDashboard = new JCheckBox("Показать дашборд", false);
    private final JLabel lblStab1 = new JLabel("-");
    private final JLabel lblStab2 = new JLabel("-");
    private final JLabel lblMaxAll1 = new JLabel("-");
    private final JLabel lblMaxAll2 = new JLabel("-");
    private final JLabel lblMaxOver1 = new JLabel("-");
    private final JLabel lblMaxOver2 = new JLabel("-");
    private final JTextArea output = new JTextArea();
    private final JTable jumpsTable = new JTable();
    private final JumpTableModel jumpsModel = new JumpTableModel(java.util.Collections.emptyList());
    private final JPanel dashboardPanel = new JPanel(new BorderLayout());

    public TrendAppFrame() {
        super("Оценка стабильности трендов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 560);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(10,10,10,10));
        setContentPane(root);

        root.add(buildTopPanel(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10,10));

        dashboardPanel.setBorder(BorderFactory.createTitledBorder("Дашборд по последнему анализу"));
        dashboardPanel.add(buildDashboard(), BorderLayout.CENTER);
        dashboardPanel.setVisible(false);
        center.add(dashboardPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        tabs.add("Отчёт", new JScrollPane(output));

        jumpsTable.setModel(jumpsModel);
        jumpsTable.setAutoCreateRowSorter(true);
        tabs.add("Скачки (таблица)", new JScrollPane(jumpsTable));

        center.add(tabs, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
    }

    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        c.gridy = 0;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        top.add(new JLabel("CSV файл:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        fileField.setEditable(false);
        top.add(fileField, c);

        JButton browse = new JButton("Обзор...");
        browse.addActionListener(e -> onBrowse());
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        top.add(browse, c);

        c.gridy = 1;
        c.gridx = 0;
        top.add(new JLabel("Порог скачка:"), c);

        c.gridx = 1;
        ((JSpinner.DefaultEditor) thresholdSpinner.getEditor()).getTextField().setColumns(6);
        top.add(thresholdSpinner, c);

        JButton analyze = new JButton("Анализ");
        analyze.addActionListener(e -> onAnalyze());
        c.gridx = 2;
        top.add(analyze, c);
        c.gridy = 0;
        c.gridx = 3;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHEAST;
        chkShowDashboard.addActionListener(e -> {
            dashboardPanel.setVisible(chkShowDashboard.isSelected());
            dashboardPanel.revalidate();
            dashboardPanel.repaint();
        });
        top.add(chkShowDashboard, c);

        return top;
    }

    private JPanel buildDashboard() {
        JPanel p = new JPanel(new GridLayout(1,2,10,10));
        p.add(buildTrendCard("Тренд 1", lblStab1, lblMaxAll1, lblMaxOver1));
        p.add(buildTrendCard("Тренд 2", lblStab2, lblMaxAll2, lblMaxOver2));
        return p;
    }

    private JPanel buildTrendCard(String title, JLabel lblStab, JLabel lblMaxAll, JLabel lblMaxOver) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder(title));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0;

        Font big = new Font(Font.SANS_SERIF, Font.BOLD, 16);

        card.add(new JLabel("Стабильность, %"), c);
        c.gridx = 1; card.add(lblStab, c); lblStab.setFont(big);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Макс. скачок"), c);
        c.gridx = 1; card.add(lblMaxAll, c); lblMaxAll.setFont(big);

        c.gridx = 0; c.gridy++;
        card.add(new JLabel("Мин. скачок (> порога)"), c);
        c.gridx = 1; card.add(lblMaxOver, c); lblMaxOver.setFont(big);

        return card;
    }

    private void onBrowse() {
        JFileChooser ch = new JFileChooser();
        int res = ch.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = ch.getSelectedFile();
            fileField.setText(f.getAbsolutePath());
        }
    }

    private void onAnalyze() {
        String path = fileField.getText().trim();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Выберите CSV файл",
                    "Нет файла", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double threshold = ((Number) thresholdSpinner.getValue()).doubleValue();

        try {
            CsvLoader.DataSet ds = CsvLoader.load(Path.of(path));
            StabilityResult r1 = TrendAnalyzer.analyze(ds.trend1, ds.times, threshold);
            StabilityResult r2 = TrendAnalyzer.analyze(ds.trend2, ds.times, threshold);

            double minOver1 = Double.MAX_VALUE;
            for (Jump j : r1.jumps) {
                if (j.diff < minOver1) minOver1 = j.diff;
            }
            if (minOver1 == Double.MAX_VALUE) minOver1 = 0.0;

            double minOver2 = Double.MAX_VALUE;
            for (Jump j : r2.jumps) {
                if (j.diff < minOver2) minOver2 = j.diff;
            }
            if (minOver2 == Double.MAX_VALUE) minOver2 = 0.0;

            StringBuilder sb = new StringBuilder();
            sb.append("Результат анализа\n");
            sb.append("Файл: ").append(path).append("\n");
            sb.append("Порог скачка: ").append(threshold).append("\n\n");
            sb.append(reportFor(ds.trend1Name, r1, minOver1)).append("\n");
            sb.append(reportFor(ds.trend2Name, r2, minOver2));
            output.setText(sb.toString());

            if (chkShowDashboard.isSelected()) {
                lblStab1.setText(String.format("%.2f", r1.getStabilityPercent()));
                lblStab2.setText(String.format("%.2f", r2.getStabilityPercent()));
                lblMaxAll1.setText(String.format("%.3f", r1.maxJump));
                lblMaxAll2.setText(String.format("%.3f", r2.maxJump));
                lblMaxOver1.setText(r1.getJumpPairs() > 0 ? String.format("%.3f", minOver1) : "-");
                lblMaxOver2.setText(r2.getJumpPairs() > 0 ? String.format("%.3f", minOver2) : "-");
                dashboardPanel.setVisible(true);
            }

            List<JumpRow> rows = new ArrayList<>();
            for (Jump j : r1.jumps) rows.add(new JumpRow(ds.trend1Name, j.indexFrom, j.timeFrom, j.timeTo, j.diff));
            for (Jump j : r2.jumps) rows.add(new JumpRow(ds.trend2Name, j.indexFrom, j.timeFrom, j.timeTo, j.diff));
            jumpsModel.setRows(rows);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(),
                    "Не удалось прочитать файл", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String reportFor(String name, StabilityResult r, double minOver) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(String.format("%nВсего пар точек: %d%n", r.totalPairs));
        sb.append(String.format("Стабильных пар: %d (%.2f%%)%n", r.stablePairs, r.getStabilityPercent()));
        sb.append(String.format("Скачков: %d%n", r.getJumpPairs()));
        sb.append(String.format("Максимальный скачок: %.3f%n", r.maxJump));
        if (r.getJumpPairs() > 0) {
            sb.append(String.format("Минимальный скачок (> порога): %.3f%n", minOver));
        }
        return sb.toString();
    }
}
