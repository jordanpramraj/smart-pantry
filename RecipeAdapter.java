package com.example.assignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


 // Binds a list of Recipe objects to rows in the Suggested Recipes RecyclerView.

 // Each row shows the recipe name and its ingredient count.
 
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClick {
        void onClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClick listener;

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClick listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Recipe r = recipes.get(position);
        h.name.setText(r.getName());
        h.count.setText(r.getIngredients().size() + " ingredients");

        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView count;

        VH(@NonNull View itemView) {
            super(itemView);
            name  = itemView.findViewById(R.id.tvRecipeName);
            count = itemView.findViewById(R.id.tvIngredientCount);
        }
    }
}
