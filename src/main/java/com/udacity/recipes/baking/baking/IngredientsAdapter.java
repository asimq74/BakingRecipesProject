package com.udacity.recipes.baking.baking;

import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.recipes.baking.baking.businessObjects.Ingredient;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to display list of ingredients
 *
 * @author Asim Qureshi
 */
public class IngredientsAdapter
		extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

	class IngredientsViewHolder extends RecyclerView.ViewHolder {

		@BindView(R.id.ingredient)
		TextView mIngredientView;

		IngredientsViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}

	@Inject
	FormatterApi formatterApi;
	private final List<Ingredient> ingredients;

	public IngredientsAdapter(@NonNull final Context context, @NonNull final List<Ingredient> ingredients) {
		this.ingredients = ingredients;
		((MyApplication) context.getApplicationContext()).getApplicationComponent().inject(this);
	}

	@Override
	public int getItemCount() {
		return ingredients.size();
	}

	@Override
	public void onBindViewHolder(final IngredientsViewHolder holder, int position) {
		final Ingredient ingredient = ingredients.get(position);
		holder.mIngredientView.setText(formatterApi.formatIngredientForDisplay(ingredient));
		holder.itemView.setTag(ingredient);
	}

	@Override
	public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.ingredient_list_item, parent, false);
		return new IngredientsViewHolder(view);
	}
}
