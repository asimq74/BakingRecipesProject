package com.udacity.recipes.baking.baking.dependencies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;

public interface RecipeIngredientsWidgetApi {

	Intent buildWidgetClickIntent(@NonNull Context context, @NonNull Recipe recipe);

}
