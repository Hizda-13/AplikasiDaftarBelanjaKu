package com.ryanjuniarto.daftarbelanjaku;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private ArrayList<Item> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onCheckClick(int position, boolean isChecked);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iconCategory;
        public TextView textName, textQuantity, textDate, textPriority;
        public CheckBox checkBox;
        public View priorityIndicator;
        public ImageView btnEdit, btnDelete;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            iconCategory = itemView.findViewById(R.id.icon_category);
            textName = itemView.findViewById(R.id.text_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textDate = itemView.findViewById(R.id.text_date);
            textPriority = itemView.findViewById(R.id.text_priority);
            checkBox = itemView.findViewById(R.id.checkbox);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // Item click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            // Checkbox click
            checkBox.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onCheckClick(position, checkBox.isChecked());
                    }
                }
            });

            // Edit button click
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            // Delete button click
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    public ItemAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item currentItem = itemList.get(position);

        // Set icon based on category
        holder.iconCategory.setImageResource(currentItem.getCategoryIcon());

        // Set text
        holder.textName.setText(currentItem.getName());
        holder.textQuantity.setText(currentItem.getQuantity());
        holder.textDate.setText(currentItem.getDate());

        // Set priority text
        String priorityText = "";
        switch (currentItem.getPriority()) {
            case 3: priorityText = "High"; break;
            case 2: priorityText = "Medium"; break;
            default: priorityText = "Low"; break;
        }
        holder.textPriority.setText(priorityText);

        // Set priority indicator color
        holder.priorityIndicator.setBackgroundResource(currentItem.getPriorityColor());

        // Set checkbox
        holder.checkBox.setChecked(currentItem.getIsChecked());

        // Strike-through text if checked
        if (currentItem.getIsChecked()) {
            holder.textName.setPaintFlags(holder.textName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textQuantity.setPaintFlags(holder.textQuantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textName.setPaintFlags(holder.textName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textQuantity.setPaintFlags(holder.textQuantity.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}