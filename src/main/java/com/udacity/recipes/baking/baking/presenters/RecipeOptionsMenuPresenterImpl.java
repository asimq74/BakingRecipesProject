package com.udacity.recipes.baking.baking.presenters;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.recipes.baking.baking.MainActivity;
import com.udacity.recipes.baking.baking.R;
import com.udacity.recipes.baking.baking.StepNavigationCallback;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApi;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Implementation of the RecipeOptionsMenuPresenter API
 *
 * @author Asim Qureshi
 */
public class RecipeOptionsMenuPresenterImpl implements RecipeOptionsMenuPresenter {

	private final IngredientsAndDescriptionIntentGenerator ingredientsAndDescriptionIntentGenerator;
	private final ResourceOverridesApi resourceOverridesApi;

	public RecipeOptionsMenuPresenterImpl(IngredientsAndDescriptionIntentGenerator ingredientsAndDescriptionIntentGenerator,
			ResourceOverridesApi resourceOverridesApi) {
		this.ingredientsAndDescriptionIntentGenerator = ingredientsAndDescriptionIntentGenerator;
		this.resourceOverridesApi = resourceOverridesApi;
	}

	public OnNavigationItemSelectedListener createOnNavigationItemSelectedListener(@NonNull final AppCompatActivity activity) {
		if (!(activity instanceof StepNavigationCallback)) {
			return new OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					return false;
				}
			};
		}
		final StepNavigationCallback callback = (StepNavigationCallback) activity;
		return new OnNavigationItemSelectedListener() {

			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				switch (item.getItemId()) {
					case R.id.navigation_prev:
						callback.loadNewStep(Sort.DESCENDING);
						return true;
					case R.id.navigation_next:
						callback.loadNewStep(Sort.ASCENDING);
						return true;
					case R.id.navigation_home:
						Intent intent = new Intent(activity, MainActivity.class);
						activity.startActivity(intent);
						return true;
				}
				return false;
			}

		};
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull AppCompatActivity activity, @NonNull MenuItem item) {
		activity.finish();
		Intent intent = ingredientsAndDescriptionIntentGenerator.setUpIngredientsAndDescriptionIntent(item.getItemId());
		activity.startActivity(intent);
		return false;
	}

	/**
	 * Gets called every time the user presses the menu button.
	 * Use if your menu is dynamic.
	 */
	@Override
	public void onPrepareOptionsMenu(@NonNull AppCompatActivity activity, @NonNull Menu menu) {
		Realm realm = Realm.getDefaultInstance();
		List<Recipe> recipes = new ArrayList<>();
		RealmQuery<Recipe> query = realm.where(Recipe.class);
		RealmResults<Recipe> results = query.findAll();
		recipes.addAll(realm.copyFromRealm(results));
		menu.clear();
		for (Recipe recipe : recipes) {
			final int iconResource = recipe.getIconResource() == 0
					? resourceOverridesApi.getRecipeIconOverrideMap().get(recipe.getName()) : recipe.getIconResource();
			menu.add(0, recipe.getId(), Menu.NONE, recipe.getName()).setIcon(iconResource);
		}
		ActionBar actionBar = activity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
	}

}
