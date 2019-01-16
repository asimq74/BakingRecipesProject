package com.udacity.recipes.baking.baking.presenters;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApi;

/**
 * Implementation of the ToolbarPresenter API
 *
 * @author Asim Qureshi
 */
public class ToolbarPresenterImpl implements ToolbarPresenter {

	private static final RequestOptions REQUEST_OPTIONS = new RequestOptions().override(300, 300);
	private final FormatterApi formatterApi;
	private final ResourceOverridesApi resourceOverridesApi;

	public ToolbarPresenterImpl(FormatterApi formatterApi, ResourceOverridesApi resourceOverridesApi) {
		this.formatterApi = formatterApi;
		this.resourceOverridesApi = resourceOverridesApi;
	}

	@Override
	public void populateToolbar(@NonNull AppCompatActivity activity, @NonNull Recipe recipe, @NonNull Toolbar toolbar,
			@NonNull TextView recipeTitleView, @NonNull TextView yieldDescription, @NonNull ImageView mRecipeImageView,
			int backgroundColor, int titleTextColor) {
		final String recipeName = recipe.getName();
		toolbar.setTitle(recipeName);
		toolbar.setBackgroundColor(backgroundColor);
		toolbar.setTitleTextColor(titleTextColor);
		activity.setSupportActionBar(toolbar);
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		recipeTitleView.setText(recipeName);
//		handle the images if present.
		final String imageUrlString = recipe.getImage().isEmpty()
				? resourceOverridesApi.getRecipeImageOverrideMap().get(recipeName) : recipe.getImage();
		yieldDescription.setText(formatterApi.formatServings(recipe.getServings()));
		Glide.with(activity)
				.load(imageUrlString)
				.apply(REQUEST_OPTIONS)
				.into(mRecipeImageView);
	}

}
