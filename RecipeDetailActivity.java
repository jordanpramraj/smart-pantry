package com.example.assignment;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


//  Displays the full ingredient list and preparation steps for a selected recipe.
 
public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        long recipeId = getIntent().getLongExtra("recipe_id", -1);

        TextView title       = findViewById(R.id.tvRecipeTitle);
        TextView ingredients = findViewById(R.id.tvIngredientsList);
        TextView steps       = findViewById(R.id.tvSteps);

        Recipe recipe = new DatabaseHelper(this).getRecipeById(recipeId);

        if (recipe == null) {
            title.setText("Recipe not found");
            return;
        }

        title.setText(recipe.getName());

        StringBuilder sb = new StringBuilder();
        for (Recipe.Ingredient ing : recipe.getIngredients()) {
            sb.append("• ")
                    .append(ing.getName())
                    .append(" — ")
                    .append(ing.getQuantity())
                    .append(" ")
                    .append(ing.getUnit() == null ? "" : ing.getUnit())
                    .append("\n");
        }
        ingredients.setText(sb.toString());
        steps.setText(recipe.getSteps());
    }
}
