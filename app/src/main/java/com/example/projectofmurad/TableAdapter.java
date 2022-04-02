package com.example.projectofmurad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private Context context;

    private final ArrayList<String> cells;

    public TableAdapter(Context context, ArrayList<String> cells) {
        super();

        this.context = context;
        this.cells = cells;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_cell, parent, false);

        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.et_cell.setText(cells.get(position));
    }

    @Override
    public int getItemCount() {
        return cells.size();
    }

    public class TableViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        EditText et_cell;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);

            et_cell = itemView.findViewById(R.id.et_cell);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
