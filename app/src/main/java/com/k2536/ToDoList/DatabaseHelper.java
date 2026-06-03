package com.k2536.ToDoList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todolist.db";
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "is_completed INTEGER NOT NULL DEFAULT 0," +
                "due_date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM tasks ORDER BY is_completed ASC", null);
        while (c.moveToNext()) {
            list.add(new Task(
                    c.getInt(0), c.getString(1), c.getString(2),
                    c.getInt(3) == 1, c.getString(4)));
        }
        c.close();
        return list;
    }

    public long insertTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put("title", task.getTitle());
        cv.put("description", task.getDescription());
        cv.put("is_completed", task.isCompleted() ? 1 : 0);
        cv.put("due_date", task.getDueDate());
        return getWritableDatabase().insert("tasks", null, cv);
    }

    public void updateTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put("title", task.getTitle());
        cv.put("description", task.getDescription());
        cv.put("is_completed", task.isCompleted() ? 1 : 0);
        cv.put("due_date", task.getDueDate());
        getWritableDatabase().update("tasks", cv, "id=?", new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(int id) {
        getWritableDatabase().delete("tasks", "id=?", new String[]{String.valueOf(id)});
    }
}
