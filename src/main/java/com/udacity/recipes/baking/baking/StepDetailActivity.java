package com.udacity.recipes.baking.baking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.recipes.baking.baking.businessObjects.Step;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;
import com.udacity.recipes.baking.baking.presenters.RecipeOptionsMenuPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Sort;

/**
 * Activity displaying the step detail
 */
public class StepDetailActivity extends BaseActivity implements StepNavigationCallback, BakingConstants {

	private final String TAG = this.getClass().getSimpleName();
	@BindView(R.id.navigation)
	BottomNavigationView navigationView;
	private int recipeId;
	@Inject
	RecipeOptionsMenuPresenter recipeOptionsMenuPresenter;
	private Step step = new Step();
	private List<Step> steps = new ArrayList<>();
	@Inject
	StepsAdapterPositionApi stepsAdapterPositionApi;

	@Override
	public OnNavigationItemSelectedListener createOnNavigationItemSelectedListener() {
		return recipeOptionsMenuPresenter.createOnNavigationItemSelectedListener(this);
	}

	@Override
	public void loadNewStep(@NonNull Sort sort) {
		if (sort.equals(Sort.ASCENDING)) {
			int position = 0;
			for (Step stepToNavigate : steps) {
				if (stepToNavigate.getId() > step.getId()) {
					Log.d(TAG, String.format("new step: %s", stepToNavigate));
					updateSelectedStep(stepToNavigate, position);
					loadNewStep(stepToNavigate, recipeId, steps);
					break;
				}
				position++;
			}
		} else {
			for (int counter = steps.size() - 1; counter >= 0; counter--) {
				Step stepToNavigate = steps.get(counter);
				if (stepToNavigate.getId() < step.getId()) {
					Log.d(TAG, String.format("new step: %s", stepToNavigate));
					updateSelectedStep(stepToNavigate, counter);
					loadNewStep(stepToNavigate, recipeId, steps);
					break;
				}
			}
		}
	}

	@Override
	public void loadNewStep(@NonNull Step step, int recipeId, @NonNull List<Step> steps) {
		Bundle args = new Bundle();
		args.putParcelable(STEP_EXTRA, step);
		final Step[] stepsArray = (Step[]) steps.toArray();
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
		} else if (stepsAdapterPositionApi.getStepsAdapterPosition() == steps.size() - 1) {
			menu.removeItem(R.id.navigation_next);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, IngredientsAndDescriptionListActivity.class);
		intent.putExtra(RECIPE_ID, recipeId);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final MyApplication application = (MyApplication) this.getApplication();
		application.getApplicationComponent().inject(this);
		setContentView(R.layout.activity_step_detail);
		ButterKnife.bind(this);
		step = getIntent().getParcelableExtra(STEP_EXTRA);
		recipeId = getIntent().getIntExtra(RECIPE_ID, 1);
		final Parcelable[] parcelableArray = getIntent().getParcelableArrayExtra(ARG_STEPS_ARRAY);
		Step[] convertedSteps = Arrays.copyOf(parcelableArray, parcelableArray.length, Step[].class);
		steps = Arrays.asList(convertedSteps);
		navigationView.setOnNavigationItemSelectedListener(createOnNavigationItemSelectedListener());
		populateToolbar(recipeId);
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putParcelable(STEP_EXTRA, step);
			arguments.putInt(RECIPE_ID, recipeId);
			arguments.putParcelableArray(ARG_STEPS_ARRAY, steps.toArray(new Step[steps.size()]));
			StepDetailFragment stepDetailFragment = new StepDetailFragment();
			stepDetailFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.steps_detail_container, stepDetailFragment).commit();
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
	public void updateSelectedStep(@NonNull Step step, int position) {
		stepsAdapterPositionApi.setStepsAdapterPosition(position);
	}
}

