package com.example.trend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

public class TrendAppFrame extends JFrame {

    private final JTextField fileField = new JTextField();
    private final JSpinner thresholdSpinner =
            new JSpinner(new SpinnerNumberModel(3.0, 0.0, 10000.0, 0.1));
    private final JTextArea output = new JTextArea();

    public TrendAppFrame() {
        super("Оценка стабильности трендов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(buildTopPanel(), BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Отчёт", new JScrollPane(output));
        root.add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTopPanel() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridy = 0; c.gridx = 0; c.anchor = GridBagConstraints.WEST;
        top.add(new JLabel("CSV файл:"), c);

        c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.HORIZONTAL;
        fileField.setEditable(false);
        top.add(fileField, c);

        JButton browse = new JButton("Обзор…");
        browse.addActionListener(e -> onBrowse());
        c.gridx = 2; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        top.add(browse, c);

        c.gridy = 1; c.gridx = 0;
        top.add(new JLabel("Порог скачка:"), c);

        c.gridx = 1;
        ((JSpinner.DefaultEditor) thresholdSpinner.getEditor()).getTextField().setColumns(6);
        top.add(thresholdSpinner, c);

        JButton analyze = new JButton("Анализ");
        analyze.addActionListener(e -> onAnalyze());
        c.gridx = 2;
        top.add(analyze, c);

        return top;
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
        double threshold = ((Number) thresholdSpinner.getValue()).doubleValue();

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Выберите CSV файл",
                    "Нет файла", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            CsvLoader.DataSet ds = CsvLoader.load(Path.of(path));

            int n1 = ds.trend1.size();
            int n2 = ds.trend2.size();

            StringBuilder sb = new StringBuilder();
            sb.append("Результат анализа\n");
            sb.append("Файл: ").append(path).append('\n');
            sb.append("Порог скачка: ").append(threshold).append("\n\n");

            sb.append("Тренды:\n");
            sb.append(" - ").append(ds.trend1Name).append(": ").append(n1).append(" точек\n");
            sb.append(" - ").append(ds.trend2Name).append(": ").append(n2).append(" точек\n");

            output.setText(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Ошибка при чтении CSV: " + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
