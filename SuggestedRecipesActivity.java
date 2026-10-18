package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


 // Runs the strict matching algorithm against the user's current pantry and
 // displays only the recipes the user can genuinely cook right now. Shows an empty-state message when no recipes match.
 
public class SuggestedRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        DatabaseHelper db = new DatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.recyclerRecipes);
        TextView emptyView = findViewById(R.id.tvNoRecipes);

        List<PantryItem> pantry = db.getAllPantryItems();
        List<Recipe>     all    = db.getAllRecipes();
        List<Recipe>     matches = new ArrayList<>();

        for (Recipe r : all) {
            if (IngredientMatcher.canCook(r, pantry)) {
                matches.add(r);
            }
        }

        if (matches.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecipeAdapter(matches, recipe -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("recipe_id", recipe.getId());
            startActivity(i);
        }));
    }
}
