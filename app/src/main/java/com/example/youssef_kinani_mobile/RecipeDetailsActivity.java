package com.example.youssef_kinani_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView recipeNameText, ingredientsText, stepsText, categoryText;
    private ImageView recipeImage;
    private Button deleteButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeNameText = findViewById(R.id.recipe_name);
        ingredientsText = findViewById(R.id.recipe_ingredients);
        stepsText = findViewById(R.id.recipe_steps);
        categoryText = findViewById(R.id.recipe_category);
        recipeImage = findViewById(R.id.recipe_image);
        deleteButton = findViewById(R.id.delete_button);
        editButton = findViewById(R.id.edit_button);

        String recipeName = getIntent().getStringExtra("recipeName");

        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        String recipeData = preferences.getString(recipeName, null);

        if (recipeData != null) {
            String[] parts = recipeData.split("\\|");

            recipeNameText.setText(parts[0]);
            ingredientsText.setText("Ingrédients:\n" + parts[1]);
            stepsText.setText("Étapes:\n" + parts[2]);

            if (parts.length > 3) {
                categoryText.setText("Catégorie : " + parts[3]);
            } else {
                categoryText.setText("Catégorie : Non spécifiée");
            }

            if (parts.length > 4 ) {
                
                recipeImage.setImageResource(R.drawable.chabakiya);
                recipeImage.setVisibility(View.VISIBLE);
            } else {
                recipeImage.setVisibility(View.GONE);
            }
        }

        
        deleteButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(recipeName);
            editor.apply();

            Toast.makeText(this, "Recette supprimée avec succès!", Toast.LENGTH_SHORT).show();
            finish();
        });

       
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeDetailsActivity.this, EditRecipeActivity.class);
            intent.putExtra("recipeName", recipeName); 
            startActivity(intent);
        });
    }
}
