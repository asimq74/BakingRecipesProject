package com.udacity.recipes.baking.baking.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.udacity.recipes.baking.baking.AlarmManagerBroadcastReceiver;
import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.R;
import com.udacity.recipes.baking.baking.businessObjects.Ingredient;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.FormatterApiImpl;
import com.udacity.recipes.baking.baking.dependencies.RecipeIngredientsWidgetApi;
import com.udacity.recipes.baking.baking.dependencies.RecipeIngredientsWidgetApiImpl;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApi;
import com.udacity.recipes.baking.baking.dependencies.RecipesFacadeApiImpl;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApi;
import com.udacity.recipes.baking.baking.dependencies.ResourceOverridesApiImpl;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Asim Qureshi.
 */

public class RecipeIngredientsAppWidgetProvider extends AppWidgetProvider implements BakingConstants {

	static final String TAG = RecipeIngredientsAppWidgetProvider.class.getSimpleName();
	static final RecipeIngredientsWidgetApi ingredientsWidgetApi = new RecipeIngredientsWidgetApiImpl();
	static final ResourceOverridesApi resourceOverridesApi = new ResourceOverridesApiImpl();

	public static void handleActionViewIngredients(@NonNull final Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeIngredientsAppWidgetProvider.class));
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.recipe_name);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.imageView);
		updateAppWidgets(context, appWidgetManager, appWidgetIds);
	}

	public static void sendRefreshBroadcast(Context context) {
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.setComponent(new ComponentName(context, RecipeIngredientsAppWidgetProvider.class));
		context.sendBroadcast(intent);
	}

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		Realm realm = Realm.getDefaultInstance();
		List<Recipe> recipes = new ArrayList<>();
		RealmQuery<Recipe> query = realm.where(Recipe.class);
		RealmResults<Recipe> results = query.findAll();
		recipes.addAll(realm.copyFromRealm(results));
		final RecipesFacadeApi recipesFacade = new RecipesFacadeApiImpl();
		Recipe recipe = recipesFacade.anyRecipe(recipes);
		editor.putInt(PREFS_WIDGET_RECIPE_ID, recipe.getId());
		final Set<String> ingredientItems = new HashSet<>();
		final FormatterApi formatterApi = new FormatterApiImpl(context);
		for (Ingredient ingredient : recipe.getIngredients()) {
			ingredientItems.add(formatterApi.formatIngredientForDisplay(ingredient));
		}
		editor.putStringSet(PREFS_WIDGET_RECIPE_INGREDIENTS, ingredientItems);
		editor.commit();
		if (recipe == null) {
			recipe = realm.where(Recipe.class).findFirst();
		}
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_recipes_app_widget);
		final Intent widgetClickIntent = ingredientsWidgetApi.buildWidgetClickIntent(context, recipe);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, widgetClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		//Widgets allow click handlers to only launch pending intents
		remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);
		remoteViews.setTextViewText(R.id.recipe_name, recipe.getName());

		final int iconResource = recipe.getIconResource() == 0
				? resourceOverridesApi.getRecipeIconOverrideMap().get(recipe.getName()) : recipe.getIconResource();
		remoteViews.setImageViewResource(R.id.imageView, iconResource);

		Intent ingredientsIntent = new Intent(context, RecipeIngredientsAppWidgetRemoteViewsService.class);
		remoteViews.setRemoteAdapter(R.id.widgetListView, ingredientsIntent);

		// template to handle the click listener for each item
		PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(widgetClickIntent)
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

	static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onDisabled(Context context) {
		Toast.makeText(context, "onDisabled():last widget instance removed", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		//After after 3 seconds
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				60000, pi);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();
		if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
			// refresh all your widgets
			handleActionViewIngredients(context);
		}
		super.onReceive(context, intent);
	}

}
