package com.udacity.recipes.baking.baking;

import javax.inject.Inject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApi;
import com.udacity.recipes.baking.baking.presenters.ToolbarPresenter;

import butterknife.BindColor;
import butterknife.BindView;

/**
 * Base Activity containing common functionality for all activities
 *
 * @author Asim Qureshi
 */
public abstract class BaseActivity extends AppCompatActivity {

	@BindColor(R.color.colorPrimaryDark)
	int colorPrimaryDark;
	@BindView(R.id.recipeImage)
	ImageView mRecipeImageView;
	@BindView(R.id.recipeTitle)
	TextView recipeTitleView;
	@Inject
	RecipesFacadeApi recipesFacadeApi;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@Inject
	ToolbarPresenter toolbarPresenter;
	@BindColor(R.color.white)
	int white;
	@BindView(R.id.yieldDescription)
	TextView yieldDescription;

	public void populateToolbar(int mRecipeId) {
		Recipe recipe = recipesFacadeApi.loadRecipe(mRecipeId);
		populateToolbar(recipe);
	}

	public void populateToolbar(@NonNull Recipe recipe) {
		toolbarPresenter.populateToolbar(this, recipe, toolbar, recipeTitleView,
				yieldDescription, mRecipeImageView, colorPrimaryDark, white);
	}
}
