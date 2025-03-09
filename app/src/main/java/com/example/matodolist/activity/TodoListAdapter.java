package com.example.matodolist.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.matodolist.R;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TaskViewHolder> {

    private ArrayList<String> todoList;

    public TodoListAdapter(ArrayList<String> todoList) {
        this.todoList = todoList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        String task = todoList.get(position);
        holder.taskText.setText(task);


        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                todoList.remove(currentPosition);
                notifyItemRemoved(currentPosition);

                notifyItemRangeChanged(currentPosition, todoList.size() - currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskText;
        Button deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.taskText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}