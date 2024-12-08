package com.example.youssef_kinani_mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewRecipesActivity extends AppCompatActivity {

    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipeAdapter;
    private RadioGroup filterCategoryRadioGroup;
    private Button resetFilterButton;

    private List<String> recipeNames = new ArrayList<>(); // Complete list of recipes
    private List<String> filteredRecipes = new ArrayList<>(); // Displayed list
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipes);

        recipesRecyclerView = findViewById(R.id.recipes_recycler_view);
        filterCategoryRadioGroup = findViewById(R.id.filter_category_radio_group);
        resetFilterButton = findViewById(R.id.reset_filter_button);

        // Load recipes from SharedPreferences
        preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        Map<String, ?> allRecipes = preferences.getAll();
        recipeNames = new ArrayList<>(allRecipes.keySet());
        filteredRecipes.addAll(recipeNames); // Show all recipes by default

        // Set up RecyclerView
        recipeAdapter = new RecipeAdapter(this, filteredRecipes, preferences);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipesRecyclerView.setAdapter(recipeAdapter);

        // Handle category filter
        filterCategoryRadioGroup.setOnCheckedChangeListener((group, checkedId) -> filterRecipesByCategory(checkedId));

        // Reset Filter Button
        resetFilterButton.setOnClickListener(v -> resetFilter());
    }

    private void filterRecipesByCategory(int checkedId) {
        filteredRecipes.clear();

        if (checkedId == -1) {
            // No filter, show all recipes
            filteredRecipes.addAll(recipeNames);
        } else {
            String selectedCategory = "";

            // Determine selected category
            if (checkedId == R.id.filter_starter) {
                selectedCategory = "Entrée";
            } else if (checkedId == R.id.filter_main) {
                selectedCategory = "Plat principal";
            } else if (checkedId == R.id.filter_dessert) {
                selectedCategory = "Dessert";
            }

            // Filter recipes based on category
            for (String recipeName : recipeNames) {
                String recipeData = preferences.getString(recipeName, null);
                if (recipeData != null) {
                    String[] parts = recipeData.split("\\|");
                    if (parts.length > 3 && parts[3].equals(selectedCategory)) {
                        filteredRecipes.add(recipeName);
                    }
                }
            }
        }

        // Notify adapter to update RecyclerView
        recipeAdapter.notifyDataSetChanged();
    }

    private void resetFilter() {
        // Clear RadioGroup selection
        filterCategoryRadioGroup.clearCheck();

        // Show all recipes
        filteredRecipes.clear();
        filteredRecipes.addAll(recipeNames);

        // Notify adapter to update RecyclerView
        recipeAdapter.notifyDataSetChanged();
    }
}


