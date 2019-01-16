package com.udacity.recipes.baking.baking;

import java.util.List;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;

import com.udacity.recipes.baking.baking.businessObjects.Step;

import io.realm.Sort;

/**
 * Fragment/Activity callback that manages step navigation
 */
public interface StepNavigationCallback {

	OnNavigationItemSelectedListener createOnNavigationItemSelectedListener();

	void loadNewStep(@NonNull final Sort sort);

	void loadNewStep(@NonNull Step step, int recipeId, @NonNull List<Step> steps);

	void manageBottomNavigationItems();

	void updateSelectedStep(@NonNull Step step, int position);

}

