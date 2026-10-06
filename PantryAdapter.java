package com.example.assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


 // Binds a list of PantryItem objects to rows in the pantry RecyclerView.
 // Each row shows the ingredient name and quantity
 // to the OnItemClick listener provided by the hosting Activity.

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    public interface OnItemClick {
        void onClick(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClick listener;

    public PantryAdapter(List<PantryItem> items, OnItemClick listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PantryItem item = items.get(position);
        h.name.setText(item.getName());
        String unit = item.getUnit() == null ? "" : item.getUnit();
        h.qty.setText(item.getQuantity() + " " + unit);

        h.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView qty;

        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            qty  = itemView.findViewById(R.id.tvQty);
        }
    }
}
