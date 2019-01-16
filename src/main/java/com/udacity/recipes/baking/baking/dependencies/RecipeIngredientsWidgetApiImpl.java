package com.udacity.recipes.baking.baking.dependencies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.IngredientsAndDescriptionListActivity;
import com.udacity.recipes.baking.baking.MainActivity;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;

public class RecipeIngredientsWidgetApiImpl implements RecipeIngredientsWidgetApi, BakingConstants {

	@NonNull
	private static Intent buildRecipeDescriptionIntent(@NonNull Context context, @NonNull Recipe recipe) {
		Intent mainActivityIntent;
		mainActivityIntent = new Intent(context, IngredientsAndDescriptionListActivity.class);
		mainActivityIntent.putExtra(RECIPE_ID, recipe.getId());
		mainActivityIntent.putExtra(SHOW_INGREDIENTS, true);
		return mainActivityIntent;
	}

	@NonNull
	public Intent buildWidgetClickIntent(@NonNull Context context, @NonNull Recipe recipe) {
		Intent widgetClickIntent;
		if (recipe == null) {
			widgetClickIntent = new Intent(context, MainActivity.class);
		} else { // Set on click to open the corresponding detail activity
			final StepsAdapterPositionApi stepsAdapterPositionApi = new StepsAdapterPositionApiImpl(context);
			stepsAdapterPositionApi.resetStepsAdapterPosition();
			widgetClickIntent = buildRecipeDescriptionIntent(context, recipe);
		}
		return widgetClickIntent;
	}

}
