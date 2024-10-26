import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

class Expense {
    private String category;
    private double amount;
    private String date;

    public Expense(String category, double amount, String date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return date + " - " + category + ": $" + amount;
    }
}

public class ExpenseTrackerGUI {
    private ArrayList<Expense> expenses;
    private JTextArea expenseArea;
    private JTextField categoryInput;
    private JTextField amountInput;
    private JTextField dateInput;

    public ExpenseTrackerGUI() {
        expenses = new ArrayList<>();
        loadExpenses();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        expenseArea = new JTextArea();
        expenseArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(expenseArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Category:"));
        categoryInput = new JTextField();
        inputPanel.add(categoryInput);

        inputPanel.add(new JLabel("Amount:"));
        amountInput = new JTextField();
        inputPanel.add(amountInput);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateInput = new JTextField();
        inputPanel.add(dateInput);

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(new AddExpenseListener());
        inputPanel.add(addButton);

        JButton viewButton = new JButton("View Expenses");
        viewButton.addActionListener(new ViewExpensesListener());
        inputPanel.add(viewButton);

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void loadExpenses() {
        try (BufferedReader br = new BufferedReader(new FileReader("expenses.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                Expense expense = new Expense(parts[0], Double.parseDouble(parts[1]), parts[2]);
                expenses.add(expense);
            }
        } catch (IOException e) {
            // No expenses file found
        }
    }

    private void saveExpenses() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("expenses.txt"))) {
            for (Expense expense : expenses) {
                pw.println(expense.getCategory() + " | " + expense.getAmount() + " | " + expense.getDate());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving expenses.");
        }
    }

    private void updateExpenseArea() {
        expenseArea.setText("");
        for (Expense expense : expenses) {
            expenseArea.append(expense + "\n");
        }
    }

    private class AddExpenseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String category = categoryInput.getText();
            double amount;
            String date = dateInput.getText();
            try {
                amount = Double.parseDouble(amountInput.getText());
                if (!category.isEmpty() && !date.isEmpty()) {
                    expenses.add(new Expense(category, amount, date));
                    categoryInput.setText("");
                    amountInput.setText("");
                    dateInput.setText("");
                    saveExpenses();
                    JOptionPane.showMessageDialog(null, "Expense added!");
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid amount.");
            }
        }
    }

    private class ViewExpensesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateExpenseArea();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerGUI::new);
    }
}
