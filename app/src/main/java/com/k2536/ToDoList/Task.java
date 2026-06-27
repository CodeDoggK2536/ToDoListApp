package com.k2536.ToDoList;

/**
 * A single to-do item that maps directly to a row in the tasks table.
 */
public class Task {
    // Maps to the INTEGER PRIMARY KEY in the database
    private int id;
    private String title;
    private String description;
    // Maps to is_completed (INTEGER, 0 or 1) in SQLite
    private boolean isCompleted;
    private String dueDate;
    // Display order, reassigned after drag-and-drop
    private int sortOrder;

    /**
     * @param id          Primary key from the database
     * @param title       Task title
     * @param description Task description
     * @param isCompleted Whether the task is marked as completed
     * @param dueDate     Due date string in "yyyy-MM-dd HH:mm" format
     * @param sortOrder   Display order index, updated after drag-and-drop
     */
    public Task(int id, String title, String description,
                boolean isCompleted, String dueDate, int sortOrder) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.sortOrder = sortOrder;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
