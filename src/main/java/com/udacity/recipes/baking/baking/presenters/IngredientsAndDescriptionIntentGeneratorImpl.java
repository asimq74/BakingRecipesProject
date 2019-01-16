package com.udacity.recipes.baking.baking.presenters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.IngredientsAndDescriptionListActivity;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;

/**
 * Implementation of the IngredientsAndDescriptionIntentGenerator API
 *
 * @author Asim Qureshi
 */
public class IngredientsAndDescriptionIntentGeneratorImpl implements IngredientsAndDescriptionIntentGenerator, BakingConstants {

	private final Context context;
	private final StepsAdapterPositionApi stepsAdapterPositionApi;

	public IngredientsAndDescriptionIntentGeneratorImpl(@NonNull Context context, @NonNull StepsAdapterPositionApi stepsAdapterPositionApi) {
		this.context = context;
		this.stepsAdapterPositionApi = stepsAdapterPositionApi;
	}

	@Override
	public Intent setUpIngredientsAndDescriptionIntent(int recipeId) {
		stepsAdapterPositionApi.resetStepsAdapterPosition();
		Intent intent = new Intent(context, IngredientsAndDescriptionListActivity.class);
		intent.putExtra(RECIPE_ID, recipeId);
		return intent;
	}

}
