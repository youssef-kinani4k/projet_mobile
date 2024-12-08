package com.example.youssef_kinani_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<String> recipeNames;
    private final Context context;
    private final SharedPreferences preferences;

    public RecipeAdapter(Context context, List<String> recipeNames, SharedPreferences preferences) {
        this.context = context;
        this.recipeNames = recipeNames;
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_card, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        String recipeName = recipeNames.get(position);
        holder.recipeTitle.setText(recipeName);

        
        String recipeData = preferences.getString(recipeName, null);
        if (recipeData != null) {
            String[] parts = recipeData.split("\\|");
            if (parts.length > 4 && !parts[4].isEmpty()) {
                Uri imageUri = Uri.parse(parts[4]);
                holder.recipeImage.setImageResource(R.drawable.chabakiya); 
            } else {
                holder.recipeImage.setImageResource(android.R.drawable.ic_menu_gallery); 
            }
        }

       
        holder.detailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecipeDetailsActivity.class);
            intent.putExtra("recipeName", recipeName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeNames.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        Button detailsButton;
        ImageView recipeImage;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            detailsButton = itemView.findViewById(R.id.details_button);
            recipeImage = itemView.findViewById(R.id.recipe_image);
        }
    }
}

