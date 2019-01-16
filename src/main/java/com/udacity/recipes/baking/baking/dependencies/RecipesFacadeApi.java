package com.udacity.recipes.baking.baking.dependencies;

import java.util.List;

import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;

/**
 * Provides application scoped functionality for loading recipes
 *
 * @author Asim Qureshi
 */
public interface RecipesFacadeApi {

	Recipe anyRecipe(@NonNull final List<Recipe> recipes);

	Recipe loadRecipe(int mRecipeId);
}
