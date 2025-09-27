package com.example.trend;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
public class JumpTableModel extends AbstractTableModel {
    private final String[] cols = {"Тренд", "Индекс", "Время до", "Время после", "Разница"};
    private List<JumpRow> rows = new ArrayList<>();
    public JumpTableModel(List<JumpRow> initial) {
        if (initial != null) rows = new ArrayList<>(initial);
    }
    public void setRows(List<JumpRow> newRows) {
        rows = new ArrayList<>(newRows);
        fireTableDataChanged();
    }
    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        JumpRow r = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.trend;
            case 1 -> r.indexFrom;
            case 2 -> r.timeFrom;
            case 3 -> r.timeTo;
            case 4 -> r.diff;
            default -> "";
        };
    }
    @Override public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 1 -> Integer.class;
            case 4 -> Double.class;
            default -> String.class;
        };
    }
}
