package com.udacity.recipes.baking.baking.presenters;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * API that provides Recipe Options Menu Functionality
 *
 * @author Asim Qureshi
 */
public interface RecipeOptionsMenuPresenter {

	OnNavigationItemSelectedListener createOnNavigationItemSelectedListener(@NonNull final AppCompatActivity activity);

	boolean onOptionsItemSelected(@NonNull AppCompatActivity activity, @NonNull MenuItem item);

	/**
	 * Gets called every time the user presses the menu button.
	 * Use if your menu is dynamic.
	 */
	void onPrepareOptionsMenu(@NonNull AppCompatActivity activity, @NonNull Menu menu);
}
