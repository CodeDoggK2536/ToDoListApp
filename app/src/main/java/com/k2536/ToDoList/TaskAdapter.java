package com.k2536.ToDoList;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> tasks;
    private OnTaskListener listener;
    private boolean multiSelectMode;
    private SparseBooleanArray selectedItems;

    public interface OnTaskListener {
        void onItemClick(Task task, int position);
        void onSelectionChanged(int count);
    }

    public TaskAdapter(List<Task> tasks, OnTaskListener listener) {
        this.tasks = tasks;
        this.listener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    public void updateData(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
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

    public int[] getSelectedPositions() {
        int[] positions = new int[selectedItems.size()];
        int index = 0;
        for (int i = 0; i < selectedItems.size(); i++) {
            positions[index++] = selectedItems.keyAt(i);
        }
        return positions;
    }

    public Task getItem(int position) {
        return tasks.get(position);
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
        listener.onSelectionChanged(selectedItems.size());
        if (selectedItems.size() == 0) {
            multiSelectMode = false;
            listener.onSelectionChanged(0);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.getTitle());

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            holder.dueDate.setText(task.getDueDate());
            holder.dueDate.setVisibility(View.VISIBLE);
        } else {
            holder.dueDate.setVisibility(View.GONE);
        }

        boolean isSelected = selectedItems.get(position, false);
        holder.cardView.setStrokeColor(isSelected
                ? Color.parseColor("#FF6200EE") : Color.TRANSPARENT);
        holder.cardView.setStrokeWidth(isSelected ? 3 : 0);

        holder.cardView.setOnClickListener(v -> {
            if (multiSelectMode) {
                toggleSelection(position);
            } else {
                listener.onItemClick(task, position);
            }
        });

        holder.cardView.setOnLongClickListener(v -> {
            if (!multiSelectMode) {
                multiSelectMode = true;
                toggleSelection(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView title;
        TextView dueDate;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            title = itemView.findViewById(R.id.text_title);
            dueDate = itemView.findViewById(R.id.text_due_date);
        }
    }
}
