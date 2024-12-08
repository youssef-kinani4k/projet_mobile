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

    
    ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                   
                    selectedImageUri = result.getData().getData();
                    recipeImagePreview.setImageURI(selectedImageUri); 
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

        
        Intent intent = getIntent();
        originalRecipeName = intent.getStringExtra("recipeName");

        
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        String recipeData = preferences.getString(originalRecipeName, null);

        if (recipeData != null) {
            String[] parts = recipeData.split("\\|");

            
            recipeNameInput.setText(parts[0]);
            ingredientsInput.setText(parts[1]);
            stepsInput.setText(parts[2]);

            
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

            
            if (parts.length > 4) {
               
                recipeImagePreview.setImageResource(R.drawable.chabakiya);
            }
        }

        
        selectImageButton.setOnClickListener(v -> {
            Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePicker.launch(imageIntent); 
        });

        
        saveChangesButton.setOnClickListener(v -> saveChanges());

        
        deleteImageButton.setOnClickListener(v -> deleteImage());
    }

    private void saveChanges() {
        String newName = recipeNameInput.getText().toString();
        String newIngredients = ingredientsInput.getText().toString();
        String newSteps = stepsInput.getText().toString();

        
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

        
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

       
        if (!originalRecipeName.equals(newName)) {
            editor.remove(originalRecipeName);
        }

        
        String updatedRecipeData = newName + "|" + newIngredients + "|" + newSteps + "|" + newCategory + "|" + (selectedImageUri != null ? selectedImageUri.toString() : "");
        editor.putString(newName, updatedRecipeData);
        editor.apply();

        Toast.makeText(this, "Modifications enregistrées avec succès!", Toast.LENGTH_SHORT).show();

        
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedRecipeName", newName);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void deleteImage() {
        selectedImageUri = null;
        recipeImagePreview.setImageResource(android.R.drawable.ic_menu_gallery); 

        
        SharedPreferences preferences = getSharedPreferences("recipes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String newName = recipeNameInput.getText().toString();
        String newIngredients = ingredientsInput.getText().toString();
        String newSteps = stepsInput.getText().toString();
        int selectedCategoryId = categoryRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedCategory = findViewById(selectedCategoryId);
        String newCategory = selectedCategory.getText().toString();

        
        String updatedRecipeData = newName + "|" + newIngredients + "|" + newSteps + "|" + newCategory + "|";
        editor.putString(newName, updatedRecipeData);
        editor.apply();

        Toast.makeText(this, "Image supprimée avec succès!", Toast.LENGTH_SHORT).show();
    }
}


