package com.udacity.recipes.baking.baking;

import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.businessObjects.Step;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;
import com.udacity.recipes.baking.baking.presenters.RecipeOptionsMenuPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Sort;

/**
 * An activity representing a list of Ingredients and Steps. This activity
 * has different presentations for handset and tablet-size devices.
 */
public class IngredientsAndDescriptionListActivity extends BaseActivity
		implements IngredientsAndDescriptionListActivityCallback, StepNavigationCallback, BakingConstants {

	final String TAG = this.getClass().getSimpleName();
	private boolean isShowIngredients = false;
	private int mRecipeId;
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	@BindView(R.id.navigation)
	BottomNavigationView navigationView;
	private Recipe recipe = new Recipe();
	@Inject
	RecipeOptionsMenuPresenter recipeOptionsMenuPresenter;
	@Inject
	StepsAdapterPositionApi stepsAdapterPositionApi;
	@BindView(R.id.steps_container)
	View stepsContainer;
	@BindView(R.id.steps_detail_container)
	@Nullable
	View stepsDetailContainer;

	@Override
	public OnNavigationItemSelectedListener createOnNavigationItemSelectedListener() {
		return recipeOptionsMenuPresenter.createOnNavigationItemSelectedListener(this);
	}

	public StepsFragment getStepsFragment() {
		return (StepsFragment) getSupportFragmentManager().findFragmentByTag(STEPS_FRAGMENT_TAG);
	}

	@Override
	public boolean isTwoPane() {
		return mTwoPane;
	}

	@Override
	public void loadNewStep(@NonNull final Sort sort) {
		final int position = stepsAdapterPositionApi.getStepsAdapterPosition();
		final Step step = recipe.getSteps().get(position);
		if (sort.equals(Sort.ASCENDING)) {
			for (Step stepToNavigate : recipe.getSteps()) {
				if (stepToNavigate.getId() > step.getId()) {
					Log.d(TAG, String.format("new step: %s", stepToNavigate));
					updateSelectedStep(stepToNavigate, position);
					loadNewStep(stepToNavigate, recipe.getId(), recipe.getSteps());
					break;
				}
			}
		} else {
			for (int counter = recipe.getSteps().size() - 1; counter >= 0; counter--) {
				Step stepToNavigate = recipe.getSteps().get(counter);
				if (stepToNavigate.getId() < step.getId()) {
					Log.d(TAG, String.format("new step: %s", stepToNavigate));
					updateSelectedStep(stepToNavigate, counter);
					loadNewStep(stepToNavigate, recipe.getId(), recipe.getSteps());
					break;
				}
			}
		}
	}

	public void loadNewStep(@NonNull Step step, int recipeId, @NonNull List<Step> steps) {
		Bundle args = new Bundle();
		args.putParcelable(STEP_EXTRA, step);
		final Step[] stepsArray = (Step[]) steps.toArray();
		args.putParcelableArray(ARG_STEPS_ARRAY, stepsArray);
		args.putInt(RECIPE_ID, recipeId);
		if (mTwoPane) {
			StepDetailFragment stepDetailFragment = new StepDetailFragment();
			stepDetailFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.steps_detail_container, stepDetailFragment);
			transaction.addToBackStack(null);
			transaction.commit();
			manageBottomNavigationItems();
			return;
		}
		Intent intent = new Intent(this, StepDetailActivity.class);
		intent.putExtra(STEP_EXTRA, step);
		intent.putExtra(RECIPE_ID, recipeId);
		intent.putExtra(ARG_STEPS_ARRAY, stepsArray);
		startActivity(intent);
	}

	@Override
	public void manageBottomNavigationItems() {
		final Menu menu = navigationView.getMenu();
		if (stepsAdapterPositionApi.getStepsAdapterPosition() == 0) {
			menu.removeItem(R.id.navigation_prev);
		} else if (stepsAdapterPositionApi.getStepsAdapterPosition() == recipe.getSteps().size() - 1) {
			menu.removeItem(R.id.navigation_next);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final MyApplication application = (MyApplication) this.getApplication();
		application.getApplicationComponent().inject(this);
		// Check whether we're recreating a previously destroyed instance
		if (savedInstanceState != null) {
			// Restore value of members from saved state
			mRecipeId = savedInstanceState.getInt(RECIPE_ID, 1);
			isShowIngredients = savedInstanceState.getBoolean(SHOW_INGREDIENTS, false);
			Log.d(TAG, String.format("savedInstanceState != null, mRecipeId: %s isShowIngredients: %s", mRecipeId, isShowIngredients));
		} else {
			Intent intent = getIntent();
			mRecipeId = intent.getIntExtra(RECIPE_ID, 1);
			isShowIngredients = intent.getBooleanExtra(SHOW_INGREDIENTS, false);
			Log.d(TAG, String.format("savedInstanceState == null, mRecipeId: %s isShowIngredients: %s", mRecipeId, isShowIngredients));
		}
		recipe = recipesFacadeApi.loadRecipe(mRecipeId);
		setContentView(R.layout.activity_ingredients_and_description);
		if (null == recipe) {
			return;
		}

		ButterKnife.bind(this);
		supportPostponeEnterTransition();
		navigationView.setOnNavigationItemSelectedListener(createOnNavigationItemSelectedListener());
		navigationView.setVisibility(getResources().getBoolean(R.bool.navBarVisible) ? View.VISIBLE : View.GONE);

		if (stepsDetailContainer != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-w900dp).
			// If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
		}
		populateToolbar(mRecipeId);
		if (stepsContainer != null) {
			Bundle args = new Bundle();
			args.putParcelableArray(ARG_STEPS_ARRAY, (Step[]) recipe.getSteps().toArray());
			args.putInt(RECIPE_ID, mRecipeId);
			args.putBoolean(SHOW_INGREDIENTS, isShowIngredients);
			StepsFragment stepsFragment = new StepsFragment();
			stepsFragment.setArguments(args);
			if (savedInstanceState == null) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.steps_container, stepsFragment, STEPS_FRAGMENT_TAG)
						.commit();
			}
		}
		if (mTwoPane) {
			Bundle args = new Bundle();
			final int stepsAdapterPosition = stepsAdapterPositionApi.getStepsAdapterPosition();
			args.putParcelable(STEP_EXTRA, recipe.getSteps().get(stepsAdapterPosition));
			args.putParcelableArray(ARG_STEPS_ARRAY, (Step[]) recipe.getSteps().toArray());
			args.putInt(RECIPE_ID, mRecipeId);
			StepDetailFragment stepDetailFragment = new StepDetailFragment();
			stepDetailFragment.setArguments(args);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.steps_detail_container, stepDetailFragment, STEP_DETAIL_FRAGMENT_TAG)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		recipeOptionsMenuPresenter.onOptionsItemSelected(this, item);
		return false;
	}

	/**
	 * Gets called every time the user presses the menu button.
	 * Use if your menu is dynamic.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		recipeOptionsMenuPresenter.onPrepareOptionsMenu(this, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(RECIPE_ID, mRecipeId);
		final int stepsAdapterPosition = stepsAdapterPositionApi.getStepsAdapterPosition();
		outState.putInt(STEP_ID, stepsAdapterPosition);
		outState.putBoolean(SHOW_INGREDIENTS, isShowIngredients);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void updateSelectedStep(@NonNull Step step, int position) {
		stepsAdapterPositionApi.setStepsAdapterPosition(position);
		getStepsFragment().highlightStep(step);
	}
}
