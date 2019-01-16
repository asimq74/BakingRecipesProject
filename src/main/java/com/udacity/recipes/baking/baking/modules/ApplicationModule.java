package com.udacity.recipes.baking.baking.modules;

import javax.inject.Singleton;

import android.app.Application;
import android.content.Context;

import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.FormatterApiImpl;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApi;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApiImpl;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApi;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApiImpl;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApiImpl;
import com.udacity.recipes.baking.baking.presenters.IngredientsAndDescriptionIntentGenerator;
import com.udacity.recipes.baking.baking.presenters.IngredientsAndDescriptionIntentGeneratorImpl;
import com.udacity.recipes.baking.baking.presenters.RecipeOptionsMenuPresenter;
import com.udacity.recipes.baking.baking.presenters.RecipeOptionsMenuPresenterImpl;
import com.udacity.recipes.baking.baking.presenters.ToolbarPresenter;
import com.udacity.recipes.baking.baking.presenters.ToolbarPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger 2 Module that provides application resources
 *
 * @author Asim Qureshi
 */
@Module
public class ApplicationModule {

	private final Application mApplication;

	public ApplicationModule(Application app) {
		mApplication = app;
	}

	@Provides
	Application provideApplication() {
		return mApplication;
	}

	@Provides
	Context provideContext() {
		return mApplication;
	}

	@Provides
	@Singleton
	FormatterApi provideFormatterApi(Context context) {
		return new FormatterApiImpl(context);
	}

	@Provides
	@Singleton
	IngredientsAndDescriptionIntentGenerator provideIngredientsAndDescriptionIntentGenerator(Context context, StepsAdapterPositionApi stepsAdapterPositionApi) {
		return new IngredientsAndDescriptionIntentGeneratorImpl(context, stepsAdapterPositionApi);
	}

	@Provides
	@Singleton
	RecipeOptionsMenuPresenter provideRecipeOptionsMenuPresenter(IngredientsAndDescriptionIntentGenerator ingredientsAndDescriptionIntentGenerator,
			ResourceOverridesApi resourceOverridesApi) {
		return new RecipeOptionsMenuPresenterImpl(ingredientsAndDescriptionIntentGenerator, resourceOverridesApi);
	}

	@Provides
	@Singleton
	RecipesFacadeApi provideRecipesFacadeApi() {
		return new RecipesFacadeApiImpl();
	}

	@Provides
	@Singleton
	ResourceOverridesApi provideResourceOverridesApi() {
		return new ResourceOverridesApiImpl();
	}

	@Provides
	@Singleton
	StepsAdapterPositionApi provideStepsAdapterPositionApi(Context context) {
		return new StepsAdapterPositionApiImpl(context);
	}

	@Provides
	@Singleton
	ToolbarPresenter provideToolbarPresenter(FormatterApi formatterApi, ResourceOverridesApi resourceOverridesApi) {
		return new ToolbarPresenterImpl(formatterApi, resourceOverridesApi);
	}

}
