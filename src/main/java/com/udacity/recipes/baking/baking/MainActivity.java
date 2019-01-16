package com.udacity.recipes.baking.baking;

import java.util.List;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;
import com.udacity.recipes.baking.baking.loaders.RecipeLoader;
import com.udacity.recipes.baking.baking.presenters.IngredientsAndDescriptionIntentGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Recipe>>, BakingConstants {

	@Inject
	IngredientsAndDescriptionIntentGenerator ingredientsAndDescriptionIntentGenerator;
	@BindView(R.id.recipesRecyclerView)
	RecyclerView recyclerView;
	@Inject
	StepsAdapterPositionApi stepsAdapterPositionApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final MyApplication application = (MyApplication) this.getApplication();
		application.getApplicationComponent().inject(this);
		stepsAdapterPositionApi.resetStepsAdapterPosition();
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		final int spanCount = getResources().getInteger(R.integer.spanCount);
		// set a GridLayoutManager with default vertical orientation
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
		recyclerView.setLayoutManager(gridLayoutManager);
		prepareLoader(RECIPE_LOADER);
	}

	@Override
	public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
		return new RecipeLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {
		RecipeAdapter recipeAdapter = new RecipeAdapter(this, data);
		recyclerView.setAdapter(recipeAdapter);
	}

	@Override
	public void onLoaderReset(Loader<List<Recipe>> loader) {

	}

	protected void prepareLoader(@NonNull final int loaderId) {
		if (getSupportLoaderManager().getLoader(loaderId) == null) {
			getSupportLoaderManager().initLoader(loaderId, null, this).forceLoad();
			return;
		}
		getSupportLoaderManager().restartLoader(loaderId, null, this).forceLoad();
	}
}
