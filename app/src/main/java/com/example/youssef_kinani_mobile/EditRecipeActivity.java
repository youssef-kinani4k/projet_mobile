package com.example.youssef_kinani_mobile;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class EditRecipeActivity extends AppCompatActivity {

    private EditText recipeNameInput, ingredientsInput, stepsInput;
    private RadioGroup categoryRadioGroup;
    private ImageView recipeImagePreview;
    private Uri selectedImageUri;
    private String originalRecipeName;

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
        setContentView(R.layout.activity_edit_recipe);

        recipeNameInput = findViewById(R.id.recipe_name_input);
        ingredientsInput = findViewById(R.id.ingredients_input);
        stepsInput = findViewById(R.id.steps_input);
        categoryRadioGroup = findViewById(R.id.category_radio_group);
        recipeImagePreview = findViewById(R.id.recipe_image_preview);
        Button saveChangesButton = findViewById(R.id.save_recipe_button);
        Button selectImageButton = findViewById(R.id.select_image_button);
        Button deleteImageButton = findViewById(R.id.delete_image_button);

        // Get the recipe name from the previous activity
        Intent intent = getIntent();
        originalRecipeName = intent.getStringExtra("recipeName");

        // Get the existing recipe data from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        String recipeData = preferences.getString(originalRecipeName, null);

        if (recipeData != null) {
            String[] parts = recipeData.split("\\|");

            // Populate the fields with existing recipe data
            recipeNameInput.setText(parts[0]);
            ingredientsInput.setText(parts[1]);
            stepsInput.setText(parts[2]);

            // Pre-select category
            if (parts.length > 3) {
                String category = parts[3];
                if (category.equals("Entrée")) {
                    categoryRadioGroup.check(R.id.category_starter);
                } else if (category.equals("Plat principal")) {
                    categoryRadioGroup.check(R.id.category_main);
                } else if (category.equals("Dessert")) {
                    categoryRadioGroup.check(R.id.category_dessert);
                }
            }

            // Display the current image if available
            if (parts.length > 4) {
                //selectedImageUri = Uri.parse(parts[4]);
                recipeImagePreview.setImageResource(R.drawable.chabakiya);
            }
        }

        // Image selection button (Gallery or Camera)
        selectImageButton.setOnClickListener(v -> {
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePicker.launch(imageIntent); // Launch the image picker
        });

        // Save changes button
        saveChangesButton.setOnClickListener(v -> saveChanges());

        // Delete image button
        deleteImageButton.setOnClickListener(v -> deleteImage());
    }

    private void saveChanges() {
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

        // Save updated data to SharedPreferences
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Remove old recipe if name has changed
        if (!originalRecipeName.equals(newName)) {
            editor.remove(originalRecipeName);
        }

        // Save updated recipe data (including the image URI)
        String updatedRecipeData = newName + "|" + newIngredients + "|" + newSteps + "|" + newCategory + "|" + (selectedImageUri != null ? selectedImageUri.toString() : "");
        editor.putString(newName, updatedRecipeData);
        editor.apply();

        Toast.makeText(this, "Modifications enregistrées avec succès!", Toast.LENGTH_SHORT).show();

        // Set the result and return the data to the main activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedRecipeName", newName);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close the activity and return to the previous one
    }

    private void deleteImage() {
        selectedImageUri = null;
        recipeImagePreview.setImageResource(android.R.drawable.ic_menu_gallery); // Set a default placeholder image

        // Save the updated data without the image URI
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String newName = recipeNameInput.getText().toString();
        String newIngredients = ingredientsInput.getText().toString();
        String newSteps = stepsInput.getText().toString();
        int selectedCategoryId = categoryRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedCategory = findViewById(selectedCategoryId);
        String newCategory = selectedCategory.getText().toString();

        // Update the data, removing the image URI
        String updatedRecipeData = newName + "|" + newIngredients + "|" + newSteps + "|" + newCategory + "|";
        editor.putString(newName, updatedRecipeData);
        editor.apply();

        Toast.makeText(this, "Image supprimée avec succès!", Toast.LENGTH_SHORT).show();
    }
}


