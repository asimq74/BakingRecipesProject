package com.udacity.recipes.baking.baking.components;

import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;

import com.udacity.recipes.baking.baking.IngredientsAdapter;
import com.udacity.recipes.baking.baking.IngredientsAndDescriptionListActivity;
import com.udacity.recipes.baking.baking.MainActivity;
import com.udacity.recipes.baking.baking.MyApplication;
import com.udacity.recipes.baking.baking.RecipeAdapter;
import com.udacity.recipes.baking.baking.StepDetailActivity;
import com.udacity.recipes.baking.baking.StepsFragment;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApi;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApi;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;
import com.udacity.recipes.baking.baking.modules.ApplicationModule;
import com.udacity.recipes.baking.baking.presenters.IngredientsAndDescriptionIntentGenerator;
import com.udacity.recipes.baking.baking.presenters.RecipeOptionsMenuPresenter;
import com.udacity.recipes.baking.baking.presenters.ToolbarPresenter;

import dagger.Component;

/**
 * Provides application resources module
 *
 * @author Asim Qureshi
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

	Application getApplication();

	Context getContext();

	FormatterApi getFormatterApi();

	RecipeOptionsMenuPresenter getRecipeOptionsMenuPresenter();

	RecipesFacadeApi getRecipesFacadeApi();

	ResourceOverridesApi getResourceOverridesApi();

	StepsAdapterPositionApi getStepsAdapterPositionApi();

	ToolbarPresenter getToolbarPresenter();

	void inject(MyApplication myApplication);

	void inject(MainActivity mainActivity);

	void inject(RecipeAdapter recipeAdapter);

	void inject(IngredientsAdapter ingredientsAdapter);

	void inject(IngredientsAndDescriptionListActivity ingredientsAndDescriptionListActivity);

	void inject(StepsFragment stepsFragment);

	void inject(StepDetailActivity stepDetailActivity);

	IngredientsAndDescriptionIntentGenerator provideIngredientsAndDescriptionIntentGenerator();

}
