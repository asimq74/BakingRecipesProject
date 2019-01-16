package com.udacity.recipes.baking.baking.dependencies;

import java.util.List;
import java.util.Random;

import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;

import io.realm.Realm;

/**
 * Implementation of RecipesFacadeApi interface
 *
 * @author Asim Qureshi
 */
public class RecipesFacadeApiImpl implements BakingConstants, RecipesFacadeApi {

	private static final Random RANDOM_GENERATOR = new Random();

	@Override
	public Recipe anyRecipe(@NonNull final List<Recipe> recipes) {
		return recipes.get(RANDOM_GENERATOR.nextInt(recipes.size()));
	}

	@Override
	public Recipe loadRecipe(int mRecipeId) {
		Realm realm = Realm.getDefaultInstance();
		return realm.where(Recipe.class).equalTo("id", mRecipeId).findFirst();
	}

}
