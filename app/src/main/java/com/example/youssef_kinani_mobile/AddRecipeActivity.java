package com.example.youssef_kinani_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText recipeNameInput, ingredientsInput, stepsInput;
    private RadioGroup categoryRadioGroup;
    private ImageView recipeImagePreview;
    private Uri selectedImageUri;

    // Image picker result launcher
    ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Get the selected image URI
                    selectedImageUri = result.getData().getData();
                    recipeImagePreview.setImageURI(selectedImageUri); // Set the image preview
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        recipeNameInput = findViewById(R.id.recipe_name_input);
        ingredientsInput = findViewById(R.id.ingredients_input);
        stepsInput = findViewById(R.id.steps_input);
        categoryRadioGroup = findViewById(R.id.category_radio_group);
        recipeImagePreview = findViewById(R.id.recipe_image_preview);
        Button saveRecipeButton = findViewById(R.id.save_recipe_button);
        Button selectImageButton = findViewById(R.id.select_image_button);

        // Image selection button (Gallery or Camera)
        selectImageButton.setOnClickListener(v -> {
            // Open the image picker
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePicker.launch(imageIntent);
        });

        // Save recipe button
        saveRecipeButton.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {
        String newName = recipeNameInput.getText().toString();
        String newIngredients = ingredientsInput.getText().toString();
        String newSteps = stepsInput.getText().toString();

        // Get selected category
        int selectedCategoryId = categoryRadioGroup.getCheckedRadioButtonId();
        if (selectedCategoryId == -1) {
            Toast.makeText(this, "Veuillez sélectionner une catégorie!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedCategory = findViewById(selectedCategoryId);
        String newCategory = selectedCategory.getText().toString();

        if (newName.isEmpty() || newIngredients.isEmpty() || newSteps.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Save the recipe data (including the image URI if selected)
        String updatedRecipeData = newName + "|" + newIngredients + "|" + newSteps + "|" + newCategory + "|" + (selectedImageUri != null ? selectedImageUri.toString() : "");
        editor.putString(newName, updatedRecipeData);
        editor.apply();

        Toast.makeText(this, "Recette ajoutée avec succès!", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity and return to the previous screen
    }
}

