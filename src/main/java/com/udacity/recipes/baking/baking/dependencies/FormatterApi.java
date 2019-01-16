package com.udacity.recipes.baking.baking.dependencies;

import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.businessObjects.Ingredient;

/**
 * API that provides various formatting functionality
 *
 * @author Asim Qureshi
 */
public interface FormatterApi {

	String formatIngredientForDisplay(@NonNull final Ingredient ingredient);

	String formatServings(int servings);

	String formatServings(@NonNull String servingsFormat, int servings);
}
