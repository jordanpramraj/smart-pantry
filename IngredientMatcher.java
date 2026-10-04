package com.example.assignment;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class IngredientMatcher {

    private IngredientMatcher() { }

    private static final Map<String, Double> UNIT_TO_BASE = new HashMap<>();
    static {
        UNIT_TO_BASE.put("g", 1.0);
        UNIT_TO_BASE.put("gram", 1.0);
        UNIT_TO_BASE.put("grams", 1.0);
        UNIT_TO_BASE.put("kg", 1000.0);
        UNIT_TO_BASE.put("kilogram", 1000.0);
        UNIT_TO_BASE.put("kilograms", 1000.0);

        UNIT_TO_BASE.put("ml", 1.0);
        UNIT_TO_BASE.put("millilitre", 1.0);
        UNIT_TO_BASE.put("millilitres", 1.0);
        UNIT_TO_BASE.put("milliliter", 1.0);
        UNIT_TO_BASE.put("milliliters", 1.0);
        UNIT_TO_BASE.put("l", 1000.0);
        UNIT_TO_BASE.put("litre", 1000.0);
        UNIT_TO_BASE.put("litres", 1000.0);
        UNIT_TO_BASE.put("liter", 1000.0);
        UNIT_TO_BASE.put("liters", 1000.0);

        UNIT_TO_BASE.put("tsp", 5.0);
        UNIT_TO_BASE.put("teaspoon", 5.0);
        UNIT_TO_BASE.put("teaspoons", 5.0);
        UNIT_TO_BASE.put("tbsp", 15.0);
        UNIT_TO_BASE.put("tablespoon", 15.0);
        UNIT_TO_BASE.put("tablespoons", 15.0);
        UNIT_TO_BASE.put("cup", 240.0);
        UNIT_TO_BASE.put("cups", 240.0);

        UNIT_TO_BASE.put("piece", 1.0);
        UNIT_TO_BASE.put("pieces", 1.0);
        UNIT_TO_BASE.put("unit", 1.0);
        UNIT_TO_BASE.put("units", 1.0);
    }

    
     //normalize an ingredient name: lowercase, trim, strip simple plural endings.
    
     //examples: "Tomatoes" -> "tomato", "Berries" -> "berry", "Eggs" -> "egg".
     
    public static String normalizeName(String name) {
        if (name == null) return "";
        String n = name.trim().toLowerCase(Locale.ROOT);
        if (n.endsWith("oes")) n = n.substring(0, n.length() - 2);           
        else if (n.endsWith("ies")) n = n.substring(0, n.length() - 3) + "y"; 
        else if (n.endsWith("s") && !n.endsWith("ss")) n = n.substring(0, n.length() - 1); 
        return n;
    }

    // convert a quantity in any supported unit to a base unit 
    public static double toBaseQuantity(double qty, String unit) {
        if (unit == null) return qty;
        Double factor = UNIT_TO_BASE.get(unit.trim().toLowerCase(Locale.ROOT));
        return factor == null ? qty : qty * factor;
    }


    public static boolean canCook(Recipe recipe, List<PantryItem> pantry) {
        for (Recipe.Ingredient req : recipe.getIngredients()) {
            String reqName = normalizeName(req.getName());
            double reqQty = toBaseQuantity(req.getQuantity(), req.getUnit());

            boolean satisfied = false;
            for (PantryItem item : pantry) {
                if (!normalizeName(item.getName()).equals(reqName)) continue;
                double haveQty = toBaseQuantity(item.getQuantity(), item.getUnit());
                if (haveQty >= reqQty) { satisfied = true; break; }
            }
            if (!satisfied) return false;
        }
        return true;
    }
}
