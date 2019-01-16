package com.udacity.recipes.baking.baking.presenters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;

/**
 * API that provides functionality for the display of the toolbar
 *
 * @author Asim Qureshi
 */
public interface ToolbarPresenter {

	void populateToolbar(@NonNull AppCompatActivity activity, @NonNull Recipe recipe, @NonNull Toolbar toolbar,
			@NonNull TextView recipeTitleView, @NonNull TextView yieldDescription, @NonNull ImageView mRecipeImageView,
			int backgroundColor, int titleTextColor);
}
