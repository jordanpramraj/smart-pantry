package com.example.assignment;

import java.util.List;


public class Recipe {
    private final long id;
    private final String name;
    private final String steps;
    private final List<Ingredient> ingredients;

    public Recipe(long id, String name, String steps, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.ingredients = ingredients;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getSteps() { return steps; }
    public List<Ingredient> getIngredients() { return ingredients; }

    public static class Ingredient {
        private final String name;
        private final double quantity;
        private final String unit;

        public Ingredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }

        public String getName() { return name; }
        public double getQuantity() { return quantity; }
        public String getUnit() { return unit; }
    }
}
