package com.k2536.ToDoList;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import java.util.List;

/**
 * RecyclerView adapter that binds the Task list to the item_task layout.
 * Supports multi-select and drag-and-drop reordering.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> tasks;
    private OnTaskListener listener;
    private boolean multiSelectMode;
    private SparseBooleanArray selectedItems;

    /**
     * Callback interface for task interactions — taps, multi-select, and completion toggles.
     */
    public interface OnTaskListener {
        void onItemClick(Task task, int position);
        void onSelectionChanged(int count);
        void onTaskCompletedChanged(Task task, boolean completed);
    }

    /**
     * Callback invoked after a drag-and-drop reorder completes.
     */
    public interface OnItemMoveListener {
        void onItemMoved();
    }

    private OnItemMoveListener moveListener;

    public TaskAdapter(List<Task> tasks, OnTaskListener listener) {
        this.tasks = tasks;
        this.listener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    /**
     * Replaces the entire data set and refreshes the display.
     * @param tasks The new task list
     */
    public void updateData(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void setOnItemMoveListener(OnItemMoveListener listener) {
        this.moveListener = listener;
    }

    /**
     * Swaps two items in the list and notifies the RecyclerView to animate the move.
     * @return true to indicate the move was handled
     */
    public boolean onItemMove(int fromPosition, int toPosition) {
        // Standard swap using a temporary variable
        Task temp = tasks.get(fromPosition);
        tasks.set(fromPosition, tasks.get(toPosition));
        tasks.set(toPosition, temp);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * Returns an ItemTouchHelper.SimpleCallback for drag-and-drop reordering.
     * Drag is disabled during multi-select mode to avoid interaction conflicts.
     */
    public ItemTouchHelper.SimpleCallback getItemTouchHelperCallback() {
        return new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                return onItemMove(from, to);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                // Disable drag gestures while multi-select is active
                if (multiSelectMode) {
                    return makeMovementFlags(0, 0);
                }
                return super.getMovementFlags(recyclerView, viewHolder);
            }

            @Override
            public void clearView(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (moveListener != null) {
                    moveListener.onItemMoved();
                }
            }
        };
    }

    /**
     * Applies visual styling for completed tasks — reduced opacity and strikethrough text.
     */
    private void applyTaskStyle(ViewHolder holder, Task task) {
        boolean completed = task.isCompleted();
        holder.title.setAlpha(completed ? 0.55f : 1f);
        holder.dueDate.setAlpha(completed ? 0.55f : 1f);
        // Add strikethrough for completed tasks, remove it when un-completed
        holder.title.setPaintFlags(completed
                ? holder.title.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                : holder.title.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public boolean isMultiSelectMode() {
        return multiSelectMode;
    }

    public void setMultiSelectMode(boolean multiSelectMode) {
        this.multiSelectMode = multiSelectMode;
        if (!multiSelectMode) {
            selectedItems.clear();
        }
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedItems.clear();
        multiSelectMode = false;
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItems.size();
    }

    /**
     * @return Array of adapter positions currently selected in multi-select mode
     */
    public int[] getSelectedPositions() {
        int[] positions = new int[selectedItems.size()];
        int index = 0;
        for (int i = 0; i < selectedItems.size(); i++) {
            positions[index++] = selectedItems.keyAt(i);
        }
        return positions;
    }

    /**
     * Returns the Task at the given adapter position.
     */
    public Task getItem(int position) {
        return tasks.get(position);
    }

    /**
     * Toggles whether the item at this position is selected.
     * Automatically backs out of multi-select mode when nothing is selected anymore.
     */
    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        int count = selectedItems.size();
        if (count == 0) {
            multiSelectMode = false;
        }
        notifyItemChanged(position);
        listener.onSelectionChanged(count);
    }

    /**
     * Inflates the item_task layout and wraps it in a ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Binds a Task's data to the ViewHolder — title, due date, completion state, and selection highlight.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.getTitle());
        holder.completedCheck.setContentDescription(task.isCompleted()
                ? "Mark task as not completed"
                : "Mark task as completed");

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            holder.dueDate.setText(task.getDueDate());
            holder.dueDate.setVisibility(View.VISIBLE);
        } else {
            holder.dueDate.setVisibility(View.GONE);
        }

        applyTaskStyle(holder, task);

        boolean isSelected = selectedItems.get(position, false);
        int primaryColor = MaterialColors.getColor(
                holder.itemView,
                android.R.attr.colorPrimary
        );
        holder.cardView.setStrokeColor(isSelected ? primaryColor : Color.TRANSPARENT);
        holder.cardView.setStrokeWidth(isSelected ? 3 : 0);

        // Solid filled circle with white checkmark for completed tasks
        if (task.isCompleted()) {
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(primaryColor);
            holder.completedCheck.setBackground(circle);
            holder.completedCheck.setImageResource(R.drawable.ic_check);
            holder.completedCheck.setColorFilter(null);
        } else {
            // Outlined circle tinted with the primary color for incomplete tasks
            holder.completedCheck.setBackground(null);
            holder.completedCheck.setImageResource(R.drawable.ic_task_unchecked);
            holder.completedCheck.setColorFilter(primaryColor, PorterDuff.Mode.SRC_IN);
        }

        holder.completedCheck.setOnClickListener(v -> {
            boolean newState = !task.isCompleted();
            listener.onTaskCompletedChanged(task, newState);
        });

        // In multi-select mode, tapping toggles selection; otherwise it opens the edit dialog
        holder.cardView.setOnClickListener(v -> {
            if (multiSelectMode) {
                toggleSelection(position);
            } else {
                listener.onItemClick(task, position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * Holds onto the views from item_task so we don't keep calling findViewById.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView completedCheck;
        TextView title;
        TextView dueDate;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            completedCheck = itemView.findViewById(R.id.check_completed);
            title = itemView.findViewById(R.id.text_title);
            dueDate = itemView.findViewById(R.id.text_due_date);
        }
    }
}
