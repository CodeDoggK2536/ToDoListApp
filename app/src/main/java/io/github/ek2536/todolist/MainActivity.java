package io.github.ek2536.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

/**
 * Main activity — the core UI and interaction hub of the ToDoList app.
 * Displays tasks in a RecyclerView with support for create, edit, complete, delete, and drag-and-drop reorder.
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {
    private DatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private LinearLayout bottomBar;
    private FloatingActionButton fab;
    private TextView textEmpty;

    /**
     * Initialises the UI, database, RecyclerView, and ItemTouchHelper for drag-and-drop.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = findViewById(R.id.recycler_tasks);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        textEmpty = findViewById(R.id.text_empty);
        // Show empty-state hint only when the list has no tasks
        textEmpty.setText(getString(R.string.empty_task_hint));
        loadData();
        // Show empty-state hint only when the list has no tasks
        textEmpty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
        adapter = new TaskAdapter(tasks, this);
        recycler.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adapter.getItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recycler);

        // Persist the new order to the database after a drag completes
        adapter.setOnItemMoveListener(() -> {
            dbHelper.updateTaskOrder(tasks);
            refresh();
        });

        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showTaskDialog(null));

        bottomBar = findViewById(R.id.bottom_bar);

        MaterialButton btnDelete = findViewById(R.id.btn_delete_selected);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel_select);

        btnDelete.setOnClickListener(v -> deleteSelectedTasks());
        btnCancel.setOnClickListener(v -> exitMultiSelect());
    }

    /**
     * Pulls the latest task list from the database while keeping the same tasks reference alive.
     */
    private void loadData() {
        tasks = dbHelper.getAllTasks();
    }

    /**
     * Reloads data, refreshes the adapter, and toggles the empty-state hint.
     */
    private void refresh() {
        loadData();
        adapter.updateData(tasks);
        textEmpty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Inflates the toolbar menu (contains the Select / Done toggle).
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Updates the toolbar item title to "Done" while in multi-select, "Select" otherwise.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_select);
        if (item != null) {
            item.setTitle(adapter.isMultiSelectMode() ? "Done" : "Select");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handles the Select / Done toolbar action — enters or exits multi-select mode.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select) {
            if (adapter.isMultiSelectMode()) {
                exitMultiSelect();
            } else {
                enterMultiSelect();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Switches to multi-select mode and refreshes the toolbar so "Done" appears.
     */
    private void enterMultiSelect() {
        adapter.setMultiSelectMode(true);
        invalidateOptionsMenu();
    }

    /**
     * Opens a dialog to create or edit a task.
     * Existing values are pre-filled when editing; new tasks get placed at the end of the list.
     * @param existingTask null for create, non-null for edit
     */
    private void showTaskDialog(Task existingTask) {
        // Inflate the shared dialog layout used for both creating and editing tasks.
        View view = getLayoutInflater().inflate(R.layout.dialog_task, null);
        // Read the title input so we can validate required text before saving.
        EditText etTitle = view.findViewById(R.id.et_title);
        // Read the optional description input from the dialog.
        EditText etDesc = view.findViewById(R.id.et_description);
        // Read the due-date field, which opens a picker instead of the keyboard.
        EditText etDueDate = view.findViewById(R.id.et_due_date);

        // Make the date field trigger a picker instead of the soft keyboard
        etDueDate.setOnClickListener(v -> showDateTimePicker(etDueDate));
        // Prevent the soft keyboard from opening for the due-date field.
        etDueDate.setFocusable(false);
        // Keep the field clickable so the user can still launch the picker dialog.
        etDueDate.setClickable(true);

        // Determine whether this dialog is editing an existing task or creating a new one.
        boolean isEdit = existingTask != null;
        if (isEdit) {
            // Pre-fill the existing title so the user can update it.
            etTitle.setText(existingTask.getTitle());
            // Pre-fill the existing description to preserve the old details.
            etDesc.setText(existingTask.getDescription());
            // Pre-fill the previously selected due date and time.
            etDueDate.setText(existingTask.getDueDate());
        }

        // Update the dialog heading so the mode is obvious to the user.
        ((TextView) view.findViewById(R.id.dialog_title))
                .setText(isEdit ? "Edit Task" : "Create Task");

        // Build the dialog around the custom Material layout.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        // Show the dialog before interacting with any of its views.
        dialog.show();
        // Transparent background so the rounded card corners show cleanly
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Save the task when the user presses the positive action button.
        view.findViewById(R.id.btn_dialog_ok).setOnClickListener(v -> {
            // Trim the title so blank spaces are not treated as valid input.
            String title = etTitle.getText().toString().trim();
            // Stop here if the required title is still empty.
            if (title.isEmpty()) return;  // Don't save a task with no title

            // Build one Task object for either create mode or edit mode.
            Task task = new Task(
                    isEdit ? existingTask.getId() : 0,
                    title,
                    etDesc.getText().toString().trim(),
                    isEdit && existingTask.isCompleted(),
                    etDueDate.getText().toString().trim(),
                    isEdit ? existingTask.getSortOrder() : tasks.size()
            );

            // Update the existing row when editing a task.
            if (isEdit) {
                dbHelper.updateTask(task);
            } else {
                // Insert a new row when creating a brand-new task.
                dbHelper.insertTask(task);
            }
            // Reload the list so the latest database state appears on screen.
            refresh();
            // Close the dialog after the save operation completes.
            dialog.dismiss();
        });

        // Close the dialog without saving any changes.
        view.findViewById(R.id.btn_dialog_cancel).setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Pops up a date picker, then a time picker, and writes the chosen date-time into the EditText.
     */
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

    /**
     * Deletes the currently selected tasks.
     * This is the nested control structure — an if inside a for loop.
     */
    private void deleteSelectedTasks() {
        int[] positions = adapter.getSelectedPositions();
        int deletedCount = 0;
        // Nested control structure: selection check (if) inside repetition (for)
        for (int pos : positions) {
            Task task = adapter.getItem(pos);
            // Guard against null before attempting deletion
            if (task != null) {
                dbHelper.deleteTask(task.getId());
                deletedCount++;
            }
        }
        if (deletedCount > 0) {
            Toast.makeText(this, "Deleted " + deletedCount + " tasks", Toast.LENGTH_SHORT).show();
        }
        exitMultiSelect();
        refresh();
    }

    /**
     * Exits multi-select mode, hides the bottom action bar, and shows the FAB.
     */
    private void exitMultiSelect() {
        adapter.clearSelection();
        bottomBar.setVisibility(View.GONE);
        fab.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    /**
     * Opens the edit dialog when a non-selected task card is tapped.
     */
    @Override
    public void onItemClick(Task task, int position) {
        showTaskDialog(task);
    }

    /**
     * Shows or hides the bottom delete bar and updates the delete button count.
     * Called by the adapter whenever the selection set changes.
     */
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
            invalidateOptionsMenu();
        }
    }

    /**
     * Persists the completion toggle to the database and refreshes the list.
     */
    @Override
    public void onTaskCompletedChanged(Task task, boolean completed) {
        task.setCompleted(completed);
        dbHelper.updateTask(task);
        refresh();
    }
}
