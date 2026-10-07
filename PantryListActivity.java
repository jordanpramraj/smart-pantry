package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


 // this launcher screen shows the user's pantry and hosts the bottom navigation.
 // reloads the pantry in onResume() so any add/edit/delete performed on other
 // screens is reflected immediately when returning here.
 
public class PantryListActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        db = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerPantry);
        emptyView = findViewById(R.id.tvEmptyPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_pantry);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(this, SuggestedRecipesActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PantryItem> items = db.getAllPantryItems();

        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);

        recyclerView.setAdapter(new PantryAdapter(items, item -> {
            Intent i = new Intent(this, AddEditIngredientActivity.class);
            i.putExtra("id",     item.getId());
            i.putExtra("name",   item.getName());
            i.putExtra("qty",    item.getQuantity());
            i.putExtra("unit",   item.getUnit());
            i.putExtra("expiry", item.getExpiryDate());
            startActivity(i);
        }));
    }
}
