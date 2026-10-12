package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Handles both adding a new pantry item and editing an existing one.
 * Detects the mode by checking whether the incoming Intent carries an "id".
 * Validates: name must be non-empty, quantity must be a positive number.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQty, etUnit, etExpiry;
    private DatabaseHelper db;
    private long editingId = -1;   // -1 means "add new"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db       = new DatabaseHelper(this);
        etName   = findViewById(R.id.etName);
        etQty    = findViewById(R.id.etQty);
        etUnit   = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);
        Button btnSave   = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        Intent i = getIntent();
        if (i.hasExtra("id")) {
            editingId = i.getLongExtra("id", -1);
            etName.setText(i.getStringExtra("name"));
            etQty.setText(String.valueOf(i.getDoubleExtra("qty", 0)));
            etUnit.setText(i.getStringExtra("unit"));
            etExpiry.setText(i.getStringExtra("expiry"));
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnSave.setOnClickListener(v -> save());
        btnDelete.setOnClickListener(v -> delete());
    }

    // Reads the form, validates it, then inserts or updates.
    private void save() {
        String name   = etName.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();
        String unit   = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            etQty.setError("Quantity is required");
            etQty.requestFocus();
            return;
        }
        double qty;
        try {
            qty = Double.parseDouble(qtyStr);
        } catch (NumberFormatException e) {
            etQty.setError("Quantity must be a number");
            etQty.requestFocus();
            return;
        }
        if (qty <= 0) {
            etQty.setError("Quantity must be greater than 0");
            etQty.requestFocus();
            return;
        }

        PantryItem item = new PantryItem(
                name, qty, unit,
                expiry.isEmpty() ? null : expiry
        );

        if (editingId == -1) {
            db.addPantryItem(item);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            item.setId(editingId);
            db.updatePantryItem(item);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void delete() {
        if (editingId != -1) {
            db.deletePantryItem(editingId);
            Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
