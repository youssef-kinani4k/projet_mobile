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

    private List<String> recipeNames = new ArrayList<>(); 
    private List<String> filteredRecipes = new ArrayList<>(); 
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipes);

        recipesRecyclerView = findViewById(R.id.recipes_recycler_view);
        filterCategoryRadioGroup = findViewById(R.id.filter_category_radio_group);
        resetFilterButton = findViewById(R.id.reset_filter_button);

        
        preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        Map<String, ?> allRecipes = preferences.getAll();
        recipeNames = new ArrayList<>(allRecipes.keySet());
        filteredRecipes.addAll(recipeNames); 

       
        recipeAdapter = new RecipeAdapter(this, filteredRecipes, preferences);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipesRecyclerView.setAdapter(recipeAdapter);

       
        filterCategoryRadioGroup.setOnCheckedChangeListener((group, checkedId) -> filterRecipesByCategory(checkedId));

        
        resetFilterButton.setOnClickListener(v -> resetFilter());
    }

    private void filterRecipesByCategory(int checkedId) {
        filteredRecipes.clear();

        if (checkedId == -1) {
            
            filteredRecipes.addAll(recipeNames);
        } else {
            String selectedCategory = "";

            
            if (checkedId == R.id.filter_starter) {
                selectedCategory = "Entrée";
            } else if (checkedId == R.id.filter_main) {
                selectedCategory = "Plat principal";
            } else if (checkedId == R.id.filter_dessert) {
                selectedCategory = "Dessert";
            }

            
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

       
        recipeAdapter.notifyDataSetChanged();
    }

    private void resetFilter() {
        
        filterCategoryRadioGroup.clearCheck();

        
        filteredRecipes.clear();
        filteredRecipes.addAll(recipeNames);

        
        recipeAdapter.notifyDataSetChanged();
    }
}


