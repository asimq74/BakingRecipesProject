package com.udacity.recipes.baking.baking.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.udacity.recipes.baking.baking.R;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.businessObjects.Step;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Task that receives Recipe Information in JSON format and persists it to a Realm database
 *
 * @author Asim Qureshi
 */
public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

	public RecipeLoader(Context context) {
		super(context);
	}

	/**
	 * Fetch data from a provided network resource.
	 * @param urlString URL string representing the network resource
	 * @return List<Recipe> list of recipes
	 */
	protected List<Recipe> collectRecipes(@NonNull String urlString) {
		String fileContentString = "";
		List<Recipe> recipes = new ArrayList<>();
		try {
			fileContentString = urlString.isEmpty() ? loadJSONFromAsset(getContext()) : run(urlString);
			Gson gson = new Gson();
			Type recipeListType = new TypeToken<List<Recipe>>() {
			}.getType();
			recipes = gson.fromJson(fileContentString, recipeListType);
			for (Recipe recipe : recipes) {
				updateRecipeIdForSteps(recipe);
			}
			storeRecipes(recipes);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "caught an exception while downloading file contents ", e);
			fileContentString = loadJSONFromAsset(getContext());
		}
		return recipes;
	}

	/**
	 * Call method to fetch recipes from the provided network resource:
	 * https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json
	 * @return List<Recipe> list of recipes
	 */
	public List<Recipe> getRecipes() {
		return collectRecipes("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json");
	}

	/**
	 * Populate realm objects with recipes
	 * @return List<Recipe> list of recipes
	 */
	@Override
	public List<Recipe> loadInBackground() {
		final List<Recipe> recipes = new ArrayList<>();
		try (Realm realm = Realm.getDefaultInstance()) {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					RealmQuery<Recipe> query = realm.where(Recipe.class);
					RealmResults<Recipe> results = query.findAll();
					recipes.addAll(realm.copyFromRealm(results));
				}
			});
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "caught an exception while storing the recipes ", e);
		}
		if (recipes.isEmpty()) {
			final List<Recipe> loadedRecipes = getRecipes();
			recipes.addAll(loadedRecipes);
		}
		return recipes;
	}

	/**
	 * Fetch data from a provided asset (if network resource isn't available).
	 * @param context the application context
	 * @return String resulting JSON string
	 */
	public String loadJSONFromAsset(@NonNull Context context) {
		String json;
		try {
			InputStream is = context.getAssets().open("baking.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	/**
	 * Uses OkHttpClient to Fetch data from a provided network resource.
	 * @param url URL string representing the network resource
	 * @return String resulting JSON string
	 */
	@NonNull
	protected String run(@NonNull final String url) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	/**
	 * Stores Recipe objects in Realm database
	 * @param recipes Recipe objects to store
	 */
	public void storeRecipes(final List<Recipe> recipes) {
		try (Realm realm = Realm.getDefaultInstance()) {
			realm.executeTransaction(new Realm.Transaction() {
				@Override
				public void execute(Realm realm) {
					realm.insertOrUpdate(recipes);
				}
			});
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "caught an exception while storing recipes ", e);
		}
	}

	/**
	 * Updates Images and Icons for recipes if image resources do not exist
	 * @param recipe Recipe object
	 */
	private void updateImagesAndIconResources(@NonNull final Recipe recipe) {
		if ("Nutella Pie".equals(recipe.getName())) {
			recipe.setImage("https://www.recipeboy.com/wp-content/uploads/2016/09/No-Bake-Nutella-Pie.jpg");
			recipe.setIconResource(R.drawable.ic_pie);
		} else if ("Yellow Cake".equals(recipe.getName())) {
			recipe.setImage("https://addapinch.com/wp-content/blogs.dir/3/files/2014/08/yellow-cake-recipe-DSC_4665.jpg");
			recipe.setIconResource(R.drawable.ic_yellow_cake);
		} else if ("Brownies".equals(recipe.getName())) {
			recipe.setImage("http://www.inspiredtaste.net/wp-content/uploads/2016/06/Brownies-Recipe-2-1200.jpg");
			recipe.setIconResource(R.drawable.ic_brownie);
		} else if ("Cheesecake".equals(recipe.getName())) {
			recipe.setImage("https://d2gk7xgygi98cy.cloudfront.net/1820-3-large.jpg");
			recipe.setIconResource(R.drawable.ic_cheesecake);
		}
	}

	/**
	 * Updates Steps with the corresponding Recipe Id
	 * @param recipe Recipe object
	 */
	private void updateRecipeIdForSteps(@NonNull final Recipe recipe) {
		for (Step step : recipe.getSteps()) {
			step.setRecipeId(recipe.getId());
		}
	}

}
