package com.example.assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Pantry table 
    public static final String TABLE_PANTRY  = "pantry";
    public static final String COL_P_ID      = "_id";
    public static final String COL_P_NAME    = "name";
    public static final String COL_P_QTY     = "quantity";
    public static final String COL_P_UNIT    = "unit";
    public static final String COL_P_EXPIRY  = "expiry";

    // Recipes table
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_R_ID      = "_id";
    public static final String COL_R_NAME    = "name";
    public static final String COL_R_STEPS   = "steps";

    // Recipe ingredients table 
    public static final String TABLE_RECIPE_ING = "recipe_ingredients";
    public static final String COL_RI_ID        = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME      = "name";
    public static final String COL_RI_QTY       = "quantity";
    public static final String COL_RI_UNIT      = "unit";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_P_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_P_NAME   + " TEXT NOT NULL, " +
                COL_P_QTY    + " REAL NOT NULL, " +
                COL_P_UNIT   + " TEXT, " +
                COL_P_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_R_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_R_NAME  + " TEXT NOT NULL, " +
                COL_R_STEPS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_ING + " (" +
                COL_RI_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RI_RECIPE_ID + " INTEGER, " +
                COL_RI_NAME      + " TEXT NOT NULL, " +
                COL_RI_QTY       + " REAL NOT NULL, " +
                COL_RI_UNIT      + " TEXT)");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_ING);
        onCreate(db);
    }

    // Pantry (CRUD)

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_P_NAME,   item.getName());
        cv.put(COL_P_QTY,    item.getQuantity());
        cv.put(COL_P_UNIT,   item.getUnit());
        cv.put(COL_P_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, cv);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_PANTRY, null, null, null, null, null,
                COL_P_NAME + " ASC"
        );
        while (c.moveToNext()) {
            list.add(new PantryItem(
                    c.getLong(c.getColumnIndexOrThrow(COL_P_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_P_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_P_QTY)),
                    c.getString(c.getColumnIndexOrThrow(COL_P_UNIT)),
                    c.getString(c.getColumnIndexOrThrow(COL_P_EXPIRY))
            ));
        }
        c.close();
        return list;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_P_NAME,   item.getName());
        cv.put(COL_P_QTY,    item.getQuantity());
        cv.put(COL_P_UNIT,   item.getUnit());
        cv.put(COL_P_EXPIRY, item.getExpiryDate());
        return db.update(
                TABLE_PANTRY, cv,
                COL_P_ID + "=?",
                new String[]{ String.valueOf(item.getId()) }
        );
    }

    public void deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_PANTRY,
                COL_P_ID + "=?",
                new String[]{ String.valueOf(id) }
        );
    }
    
    // Recipes
    
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_RECIPES, null, null, null, null, null,
                COL_R_NAME + " ASC"
        );
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndexOrThrow(COL_R_ID));
            String name = c.getString(c.getColumnIndexOrThrow(COL_R_NAME));
            String steps = c.getString(c.getColumnIndexOrThrow(COL_R_STEPS));
            recipes.add(new Recipe(id, name, steps, getIngredientsForRecipe(id)));
        }
        c.close();
        return recipes;
    }

    public Recipe getRecipeById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_RECIPES, null,
                COL_R_ID + "=?",
                new String[]{ String.valueOf(id) },
                null, null, null
        );
        Recipe recipe = null;
        if (c.moveToFirst()) {
            recipe = new Recipe(
                    id,
                    c.getString(c.getColumnIndexOrThrow(COL_R_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_R_STEPS)),
                    getIngredientsForRecipe(id)
            );
        }
        c.close();
        return recipe;
    }

    private List<Recipe.Ingredient> getIngredientsForRecipe(long recipeId) {
        List<Recipe.Ingredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_RECIPE_ING, null,
                COL_RI_RECIPE_ID + "=?",
                new String[]{ String.valueOf(recipeId) },
                null, null, null
        );
        while (c.moveToNext()) {
            list.add(new Recipe.Ingredient(
                    c.getString(c.getColumnIndexOrThrow(COL_RI_NAME)),
                    c.getDouble(c.getColumnIndexOrThrow(COL_RI_QTY)),
                    c.getString(c.getColumnIndexOrThrow(COL_RI_UNIT))
            ));
        }
        c.close();
        return list;
    }

    
    //recipes 

    private void seedRecipes(SQLiteDatabase db) {

        addRecipe(db, "Chicken Curry",
                "1. Dice chicken and brown in oil.\n" +
                        "2. Add chopped onion and curry powder.\n" +
                        "3. Pour in coconut milk and simmer 20 min.\n" +
                        "4. Serve with rice.",
                new Object[][]{
                        {"chicken",       500.0, "g"},
                        {"onion",           1.0, "pieces"},
                        {"curry powder",    2.0, "tbsp"},
                        {"coconut milk",  400.0, "ml"},
                        {"rice",          200.0, "g"},
                        {"oil",             2.0, "tbsp"}
                });

        addRecipe(db, "Beef Stew",
                "1. Brown beef cubes in a pot.\n" +
                        "2. Add chopped carrot, onion and potato.\n" +
                        "3. Cover with stock and simmer 1 hour.",
                new Object[][]{
                        {"beef",    500.0, "g"},
                        {"carrot",    3.0, "pieces"},
                        {"onion",     1.0, "pieces"},
                        {"potato",    4.0, "pieces"},
                        {"stock",   500.0, "ml"}
                });

        addRecipe(db, "Chicken Salad",
                "1. Grill chicken and slice.\n" +
                        "2. Toss lettuce, tomato and cucumber.\n" +
                        "3. Drizzle with olive oil and lemon.",
                new Object[][]{
                        {"chicken",      200.0, "g"},
                        {"lettuce",      100.0, "g"},
                        {"tomato",         2.0, "pieces"},
                        {"cucumber",       1.0, "pieces"},
                        {"olive oil",      1.0, "tbsp"},
                        {"lemon",          1.0, "pieces"}
                });

        addRecipe(db, "Vegetable Soup",
                "1. Chop carrot, celery and potato.\n" +
                        "2. Boil in stock for 30 min.\n" +
                        "3. Season and serve.",
                new Object[][]{
                        {"carrot",   3.0, "pieces"},
                        {"celery",   2.0, "pieces"},
                        {"potato",   3.0, "pieces"},
                        {"stock",  800.0, "ml"},
                        {"salt",     1.0, "tsp"}
                });

        addRecipe(db, "Spaghetti Bolognese",
                "1. Brown minced beef with onion.\n" +
                        "2. Add tinned tomatoes and herbs.\n" +
                        "3. Simmer 30 min.\n" +
                        "4. Serve over spaghetti.",
                new Object[][]{
                        {"spaghetti",   300.0, "g"},
                        {"minced beef", 400.0, "g"},
                        {"onion",         1.0, "pieces"},
                        {"tomato",        4.0, "pieces"},
                        {"oil",           1.0, "tbsp"}
                });

        addRecipe(db, "Greek Salad",
                "1. Chop cucumber, tomato and red onion.\n" +
                        "2. Add olives and feta.\n" +
                        "3. Drizzle with olive oil.",
                new Object[][]{
                        {"cucumber",     1.0, "pieces"},
                        {"tomato",       3.0, "pieces"},
                        {"onion",        1.0, "pieces"},
                        {"olives",      50.0, "g"},
                        {"feta",       100.0, "g"},
                        {"olive oil",    2.0, "tbsp"}
                });

        addRecipe(db, "Banana Smoothie",
                "1. Peel bananas and slice.\n" +
                        "2. Blend with milk and honey.\n" +
                        "3. Serve chilled.",
                new Object[][]{
                        {"banana",  2.0, "pieces"},
                        {"milk",  200.0, "ml"},
                        {"honey",   1.0, "tbsp"}
                });

        addRecipe(db, "Avocado Toast",
                "1. Toast bread.\n" +
                        "2. Mash avocado with lemon and salt.\n" +
                        "3. Spread on toast.",
                new Object[][]{
                        {"bread",     2.0, "pieces"},
                        {"avocado",   1.0, "pieces"},
                        {"lemon",     1.0, "pieces"},
                        {"salt",      1.0, "tsp"}
                });

        addRecipe(db, "Pumpkin Soup",
                "1. Roast chopped pumpkin.\n" +
                        "2. Blend with stock.\n" +
                        "3. Season with salt and pepper.",
                new Object[][]{
                        {"pumpkin", 600.0, "g"},
                        {"stock",   500.0, "ml"},
                        {"salt",      1.0, "tsp"},
                        {"pepper",    1.0, "tsp"}
                });

        addRecipe(db, "Fish and Chips",
                "1. Cut potatoes into chips and fry.\n" +
                        "2. Coat fish in flour and fry.\n" +
                        "3. Serve with lemon.",
                new Object[][]{
                        {"fish",    400.0, "g"},
                        {"potato",    5.0, "pieces"},
                        {"flour",   100.0, "g"},
                        {"oil",     200.0, "ml"},
                        {"lemon",     1.0, "pieces"}
                });

        addRecipe(db, "Mushroom Risotto",
                "1. Sauté mushrooms in butter.\n" +
                        "2. Add rice and stir.\n" +
                        "3. Ladle in stock gradually until creamy.",
                new Object[][]{
                        {"rice",      300.0, "g"},
                        {"mushroom",  200.0, "g"},
                        {"butter",     30.0, "g"},
                        {"stock",     800.0, "ml"},
                        {"onion",       1.0, "pieces"}
                });

        addRecipe(db, "Bean Burrito",
                "1. Warm beans with spices.\n" +
                        "2. Fill tortillas with beans, rice and cheese.\n" +
                        "3. Roll and serve.",
                new Object[][]{
                        {"beans",    400.0, "g"},
                        {"tortilla",   4.0, "pieces"},
                        {"rice",     150.0, "g"},
                        {"cheese",   100.0, "g"},
                        {"onion",      1.0, "pieces"}
                });

        addRecipe(db, "Caprese Salad",
                "1. Slice tomato and mozzarella.\n" +
                        "2. Layer with basil leaves.\n" +
                        "3. Drizzle with olive oil.",
                new Object[][]{
                        {"tomato",       3.0, "pieces"},
                        {"mozzarella", 150.0, "g"},
                        {"basil",       10.0, "g"},
                        {"olive oil",    2.0, "tbsp"}
                });

        addRecipe(db, "Grilled Cheese and Tomato",
                "1. Butter bread.\n" +
                        "2. Add cheese and sliced tomato.\n" +
                        "3. Grill until golden.",
                new Object[][]{
                        {"bread",     2.0, "pieces"},
                        {"cheese",   60.0, "g"},
                        {"tomato",    2.0, "pieces"},
                        {"butter",   10.0, "g"}
                });

        addRecipe(db, "Oatmeal with Berries",
                "1. Cook oats in milk.\n" +
                        "2. Top with berries and honey.",
                new Object[][]{
                        {"oats",     80.0, "g"},
                        {"milk",    250.0, "ml"},
                        {"berries", 100.0, "g"},
                        {"honey",     1.0, "tbsp"}
                });

        addRecipe(db, "Egg Chutney",
                "1. Sauté onion and pepper.\n" +
                        "2. Add tomatoes, cumin and paprika.\n" +
                        "3. Crack eggs on top and simmer until set.",
                new Object[][]{
                        {"egg",        4.0, "pieces"},
                        {"tomato",     5.0, "pieces"},
                        {"onion",      1.0, "pieces"},
                        {"pepper",     1.0, "pieces"},
                        {"cumin",      1.0, "tsp"},
                        {"oil",        2.0, "tbsp"}
                });
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, Object[][] ingredients) {
        ContentValues cv = new ContentValues();
        cv.put(COL_R_NAME, name);
        cv.put(COL_R_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, cv);

        for (Object[] ing : ingredients) {
            ContentValues ic = new ContentValues();
            ic.put(COL_RI_RECIPE_ID, recipeId);
            ic.put(COL_RI_NAME,      (String) ing[0]);
            ic.put(COL_RI_QTY,       (Double) ing[1]);
            ic.put(COL_RI_UNIT,      (String) ing[2]);
            db.insert(TABLE_RECIPE_ING, null, ic);
        }
    }
}
