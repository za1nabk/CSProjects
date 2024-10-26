/*
 * To-Do List Application
 * 
 * This is a simple Java Swing application that allows users to manage their to-do tasks.
 * Users can add tasks with a description, due date, and priority (Low, Medium, High).
 * Tasks can be marked as complete and removed from the list. The application saves 
 * tasks to a text file, allowing users to persist their data between sessions.
 * 
 * Features:
 * - Add tasks with description, due date, and priority.
 * - Mark tasks as complete.
 * - Remove tasks from the list.
 * - View tasks sorted by priority.
 * - Data persistence through file storage.
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

class Task {
    private String description;
    private String dueDate;
    private String priority;
    private boolean isComplete;

    public Task(String description, String dueDate, String priority) {
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isComplete = false;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void markComplete() {
        isComplete = true;
    }

    @Override
    public String toString() {
        return (isComplete ? "[✓] " : "[ ] ") + description + " (Due: " + dueDate + ", Priority: " + priority + ")";
    }
}

public class ToDoListApp {
    private ArrayList<Task> tasks;
    private JTextArea taskArea;
    private JTextField taskInput;
    private JTextField dueDateInput;
    private JComboBox<String> priorityInput;

    public ToDoListApp() {
        tasks = new ArrayList<>();
        loadTasks();
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        taskArea = new JTextArea();
        taskArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(taskArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 3));
        
        taskInput = new JTextField();
        inputPanel.add(taskInput);

        dueDateInput = new JTextField();
        inputPanel.add(dueDateInput);

        String[] priorities = {"Low", "Medium", "High"};
        priorityInput = new JComboBox<>(priorities);
        inputPanel.add(priorityInput);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new AddTaskListener());
        inputPanel.add(addButton);

        JButton removeButton = new JButton("Remove Task");
        removeButton.addActionListener(new RemoveTaskListener());
        inputPanel.add(removeButton);
        
        JButton completeButton = new JButton("Complete Task");
        completeButton.addActionListener(new CompleteTaskListener());
        inputPanel.add(completeButton);

        frame.add(inputPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }

    private void loadTasks() {
        try (BufferedReader br = new BufferedReader(new FileReader("tasks.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" \\| ");
                Task task = new Task(parts[0], parts[1], parts[2]);
                if (parts[3].equals("1")) {
                    task.markComplete();
                }
                tasks.add(task);
            }
        } catch (IOException e) {
            // No tasks file found
        }
    }

    private void saveTasks() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("tasks.txt"))) {
            for (Task task : tasks) {
                pw.println(task.getDescription() + " | " + task.getDueDate() + " | " + task.getPriority() + " | " + (task.isComplete() ? "1" : "0"));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving tasks.");
        }
    }

    private void updateTaskArea() {
        taskArea.setText("");
        tasks.sort(Comparator.comparing(Task::getPriority).reversed());
        for (Task task : tasks) {
            taskArea.append(task + "\n");
        }
        saveTasks();
    }

    private class AddTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String taskDescription = taskInput.getText();
            String dueDate = dueDateInput.getText();
            String priority = (String) priorityInput.getSelectedItem();
            if (!taskDescription.isEmpty() && !dueDate.isEmpty()) {
                tasks.add(new Task(taskDescription, dueDate, priority));
                taskInput.setText("");
                dueDateInput.setText("");
                updateTaskArea();
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a task and due date.");
            }
        }
    }

    private class RemoveTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog("Enter task number to remove:");
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < tasks.size()) {
                    tasks.remove(index);
                    updateTaskArea();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid task number.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }
    }

    private class CompleteTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog("Enter task number to mark as complete:");
            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < tasks.size()) {
                    tasks.get(index).markComplete();
                    updateTaskArea();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid task number.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }
}
