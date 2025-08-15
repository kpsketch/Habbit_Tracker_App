import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.util.*;

public class WeeklyHabitTracker extends JFrame {

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField habitField = new JTextField(15);
    private final JComboBox<String> daySelector;
    private final JComboBox<Integer> weekSelector;
    private final Map<String, Color> habitColors = new HashMap<>();
    private final Random rand = new Random();

    public WeeklyHabitTracker() {
        setTitle("📅 Weekly Habit Tracker");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(days, 0);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setDefaultRenderer(Object.class, new HabitCellRenderer());

        JPanel topPanel = new JPanel();

        // Week Selector
        weekSelector = new JComboBox<>();
        for (int i = 1; i <= 52; i++) {
            weekSelector.addItem(i);
        }

        daySelector = new JComboBox<>(days);

        JButton addButton = new JButton("Add Habit");
        addButton.addActionListener(this::addHabit);

        JButton editButton = new JButton("Edit Habit");
        editButton.addActionListener(this::editHabit);

        JButton exportButton = new JButton("Export to TXT");
        exportButton.addActionListener(e -> exportToTxt());

        topPanel.add(new JLabel("Week:"));
        topPanel.add(weekSelector);
        topPanel.add(new JLabel("Habit:"));
        topPanel.add(habitField);
        topPanel.add(new JLabel("Day:"));
        topPanel.add(daySelector);
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(exportButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void addHabit(ActionEvent e) {
        String habit = habitField.getText().trim();
        if (habit.isEmpty()) return;
        int col = daySelector.getSelectedIndex();

        if (tableModel.getRowCount() == 0 || tableModel.getValueAt(tableModel.getRowCount() - 1, col) != null) {
            tableModel.addRow(new Object[days.length]);
        }

        for (int r = 0; r < tableModel.getRowCount(); r++) {
            if (tableModel.getValueAt(r, col) == null) {
                tableModel.setValueAt(habit, r, col);
                habitColors.putIfAbsent(habit, getRandomPastelColor());
                break;
            }
        }
        habitField.setText("");
    }

    private void editHabit(ActionEvent e) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row == -1 || col == -1) {
            JOptionPane.showMessageDialog(this, "Select a habit to edit.");
            return;
        }
        Object oldVal = tableModel.getValueAt(row, col);
        if (oldVal == null) {
            JOptionPane.showMessageDialog(this, "No habit in this cell.");
            return;
        }
        String newHabit = JOptionPane.showInputDialog(this, "Edit Habit:", oldVal.toString());
        if (newHabit != null && !newHabit.trim().isEmpty()) {
            tableModel.setValueAt(newHabit.trim(), row, col);
            habitColors.putIfAbsent(newHabit, getRandomPastelColor());
        }
    }

    private void exportToTxt() {
        int week = (int) weekSelector.getSelectedItem();
        String fileName = "Weekly_Habits_Week_" + week + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Week: " + week + "\n\n");
            for (String day : days) {
                writer.write(day + "\t");
            }
            writer.write("\n");

            for (int r = 0; r < tableModel.getRowCount(); r++) {
                for (int c = 0; c < days.length; c++) {
                    Object val = tableModel.getValueAt(r, c);
                    writer.write((val == null ? "" : val.toString()) + "\t");
                }
                writer.write("\n");
            }
            JOptionPane.showMessageDialog(this, "TXT file created: " + fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting TXT: " + ex.getMessage());
        }
    }

    private Color getRandomPastelColor() {
        int r = rand.nextInt(128) + 127;
        int g = rand.nextInt(128) + 127;
        int b = rand.nextInt(128) + 127;
        return new Color(r, g, b);
    }

    private class HabitCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                String habit = value.toString();
                cell.setBackground(habitColors.getOrDefault(habit, Color.WHITE));
            } else {
                cell.setBackground(Color.WHITE);
            }
            if (isSelected) cell.setBackground(cell.getBackground().darker());
            return cell;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeeklyHabitTracker().setVisible(true));
    }
}
