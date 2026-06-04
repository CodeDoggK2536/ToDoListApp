package com.k2536.ToDoList;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {
    private DatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private LinearLayout bottomBar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = findViewById(R.id.recycler_tasks);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        loadData();
        adapter = new TaskAdapter(tasks, this);
        recycler.setAdapter(adapter);

        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showTaskDialog(null));

        bottomBar = findViewById(R.id.bottom_bar);

        MaterialButton btnDelete = findViewById(R.id.btn_delete_selected);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel_select);

        btnDelete.setOnClickListener(v -> deleteSelectedTasks());
        btnCancel.setOnClickListener(v -> exitMultiSelect());
    }

    private void loadData() {
        tasks = dbHelper.getAllTasks();
    }

    private void refresh() {
        loadData();
        adapter.updateData(tasks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void showTaskDialog(Task existingTask) {
        View view = getLayoutInflater().inflate(R.layout.dialog_task, null);
        EditText etTitle = view.findViewById(R.id.et_title);
        EditText etDesc = view.findViewById(R.id.et_description);
        EditText etDueDate = view.findViewById(R.id.et_due_date);

        etDueDate.setOnClickListener(v -> showDateTimePicker(etDueDate));
        etDueDate.setFocusable(false);
        etDueDate.setClickable(true);

        boolean isEdit = existingTask != null;
        if (isEdit) {
            etTitle.setText(existingTask.getTitle());
            etDesc.setText(existingTask.getDescription());
            etDueDate.setText(existingTask.getDueDate());
        }

        ((TextView) view.findViewById(R.id.dialog_title))
                .setText(isEdit ? "Edit Task" : "Add Task");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        view.findViewById(R.id.btn_dialog_ok).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) return;

            Task task = new Task(
                    isEdit ? existingTask.getId() : 0,
                    title,
                    etDesc.getText().toString().trim(),
                    isEdit && existingTask.isCompleted(),
                    etDueDate.getText().toString().trim()
            );

            if (isEdit) {
                dbHelper.updateTask(task);
            } else {
                dbHelper.insertTask(task);
            }
            refresh();
            dialog.dismiss();
        });

        view.findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> dialog.dismiss());
    }

    private void showDateTimePicker(EditText et) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            int selectedYear = year;
            int selectedMonth = month;
            int selectedDay = dayOfMonth;
            new TimePickerDialog(this, (view1, hourOfDay, minute) ->
                            et.setText(String.format("%d-%02d-%02d %02d:%02d",
                                    selectedYear, selectedMonth + 1, selectedDay,
                                    hourOfDay, minute)),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            ).show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void deleteSelectedTasks() {
        int[] positions = adapter.getSelectedPositions();
        for (int pos : positions) {
            dbHelper.deleteTask(adapter.getItem(pos).getId());
        }
        exitMultiSelect();
        refresh();
    }

    private void exitMultiSelect() {
        adapter.clearSelection();
        bottomBar.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(Task task, int position) {
        showTaskDialog(task);
    }

    @Override
    public void onSelectionChanged(int count) {
        if (count > 0) {
            bottomBar.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
            MaterialButton btnDelete = findViewById(R.id.btn_delete_selected);
            btnDelete.setText("Delete (" + count + ")");
        } else {
            bottomBar.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }
}
