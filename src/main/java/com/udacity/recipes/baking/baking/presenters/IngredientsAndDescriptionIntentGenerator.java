package com.udacity.recipes.baking.baking.presenters;

import android.content.Intent;

/**
 * API that generates an IngredientsAndDescription Intent
 *
 * @author Asim Qureshi
 */
public interface IngredientsAndDescriptionIntentGenerator {

	Intent setUpIngredientsAndDescriptionIntent(int recipeId);

}
