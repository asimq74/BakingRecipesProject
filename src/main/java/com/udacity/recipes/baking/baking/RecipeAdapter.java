package com.udacity.recipes.baking.baking;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.recipes.baking.baking.RecipeAdapter.RecipeAdapterViewHolder;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApi;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter displaying a list of recipes
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapterViewHolder> {

	/**
	 * Cache of the children views for a forecast list item.
	 */
	public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, BakingConstants {

		@BindView(R.id.cv)
		CardView mCardView;
		@BindView(R.id.name)
		TextView mNameView;
		@BindView(R.id.recipeImage)
		ImageView mRecipeImageView;
		@BindView(R.id.serving)
		TextView mServingsView;

		public RecipeAdapterViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			mCardView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Log.i(TAG, "clicked me");
		}
	}

	final String TAG = this.getClass().getSimpleName();
	private final Context context;
	@Inject
	FormatterApi formatterApi;
	private final List<Recipe> recipes;
	private final RequestOptions requestOptions = new RequestOptions().override(300, 300);
	@Inject
	ResourceOverridesApi resourceOverridesApi;

	public RecipeAdapter(Context context, List<Recipe> recipes) {
		this.context = context;
		this.recipes = recipes;
		((MyApplication) context.getApplicationContext()).getApplicationComponent().inject(this);
	}

	@Override
	public int getItemCount() {
		return recipes.size();
	}

	@Override
	public void onBindViewHolder(RecipeAdapterViewHolder holder, final int position) {
		final Recipe recipe = recipes.get(position);
		final String recipeName = recipe.getName();
		holder.mNameView.setText(recipeName);
		//		handle the images if present.
		final String imageUrlString = recipe.getImage().isEmpty()
				? resourceOverridesApi.getRecipeImageOverrideMap().get(recipeName) : recipe.getImage();
		holder.mServingsView.setText(formatterApi.formatServings(recipe.getServings()));
		Glide.with(context)
				.load(imageUrlString)
				.apply(requestOptions)
				.into(holder.mRecipeImageView);
		holder.mCardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, IngredientsAndDescriptionListActivity.class);
				intent.putExtra(BakingConstants.RECIPE_ID, recipe.getId());
				context.startActivity(intent);
			}
		});
	}

	@Override
	public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// infalte the item Layout
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
		// set the view's size, margins, paddings and layout parameters
		RecipeAdapterViewHolder vh = new RecipeAdapterViewHolder(v); // pass the view to View Holder
		return vh;
	}
}
