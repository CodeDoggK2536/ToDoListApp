package com.k2536.ToDoList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the tasks table in SQLite — creating the table and running all CRUD operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Filename for the local SQLite database
    private static final String DB_NAME = "todolist.db";
    // Schema version 3 — tracks when sort_order was added
    private static final int DB_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Creates the tasks table when the database is first created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "is_completed INTEGER NOT NULL DEFAULT 0," +
                "due_date TEXT," +
                "sort_order INTEGER NOT NULL DEFAULT 0)");
    }

    /**
     * Adds the sort_order column when upgrading from v2 to v3, so existing data
     * survives the migration instead of dropping the whole table.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0");
        }
    }

    /**
     * Returns all tasks ordered by sort_order ascending, with completed tasks at the bottom.
     * @return A list of all Task objects
     */
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM tasks ORDER BY sort_order ASC, is_completed ASC", null);
        while (c.moveToNext()) {
            list.add(new Task(
                    c.getInt(0), c.getString(1), c.getString(2),
                    c.getInt(3) == 1, c.getString(4), c.getInt(5)));
        }
        c.close();
        return list;
    }

    /**
     * Inserts a new task into the database.
     * @param task The task to insert
     * @return The primary key id of the newly inserted row
     */
    public long insertTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put("title", task.getTitle());
        cv.put("description", task.getDescription());
        cv.put("is_completed", task.isCompleted() ? 1 : 0);
        cv.put("due_date", task.getDueDate());
        cv.put("sort_order", task.getSortOrder());
        return getWritableDatabase().insert("tasks", null, cv);
    }

    /**
     * Updates all fields of an existing task (including sort order).
     * @param task The modified task object
     */
    public void updateTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put("title", task.getTitle());
        cv.put("description", task.getDescription());
        cv.put("is_completed", task.isCompleted() ? 1 : 0);
        cv.put("due_date", task.getDueDate());
        cv.put("sort_order", task.getSortOrder());
        getWritableDatabase().update("tasks", cv, "id=?", new String[]{String.valueOf(task.getId())});
    }

    /**
     * Deletes a task by its primary key.
     * @param id The primary key of the task to delete
     */
    public void deleteTask(int id) {
        getWritableDatabase().delete("tasks", "id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Rewrites the sort_order for every task to match its position in the list.
     * Called after the user drags items around. Runs inside a transaction so the
     * update either fully applies or rolls back cleanly.
     * @param tasks The task list in the new display order
     */
    public void updateTaskOrder(List<Task> tasks) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < tasks.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("sort_order", i);
                db.update("tasks", cv, "id=?", new String[]{String.valueOf(tasks.get(i).getId())});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
