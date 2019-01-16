package com.udacity.recipes.baking.baking.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.R;
import com.udacity.recipes.baking.baking.businessObjects.Ingredient;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.FormatterApiImpl;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApi;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApiImpl;

/**
 * Created by Asim Qureshi.
 */

public class RecipeIngredientsAppWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, BakingConstants {

	final FormatterApi formatterApi;
	private List<String> ingredientDescriptions = new ArrayList<>();
	private final Context mContext;
	private Recipe recipe = new Recipe();
	final RecipesFacadeApi recipesFacade = new RecipesFacadeApiImpl();

	public RecipeIngredientsAppWidgetRemoteViewsFactory(Context mContext, Intent intent) {
		this.mContext = mContext;
		this.formatterApi = new FormatterApiImpl(mContext);
	}

	@Override
	public int getCount() {
		return ingredientDescriptions == null ? 0 : ingredientDescriptions.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		if (position == AdapterView.INVALID_POSITION ||
				ingredientDescriptions == null || position >= ingredientDescriptions.size()) {
			return null;
		}
		RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_row);
		remoteViews.setTextViewText(R.id.ingredientItem, ingredientDescriptions.get(position));
		return remoteViews;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		final long identityToken = Binder.clearCallingIdentity();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		final int recipeId = prefs.getInt(PREFS_WIDGET_RECIPE_ID, 0);
		recipe = recipesFacade.loadRecipe(recipeId);
		ingredientDescriptions.clear();
		for (Ingredient ingredient : recipe.getIngredients()) {
			ingredientDescriptions.add(formatterApi.formatIngredientForDisplay(ingredient));
		}
		Binder.restoreCallingIdentity(identityToken);
	}

	@Override
	public void onDestroy() {
		if (ingredientDescriptions != null) {
			ingredientDescriptions = null;
		}
	}
}
