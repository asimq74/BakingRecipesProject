package com.udacity.recipes.baking.baking;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by U1C306 on 1/22/2018.
 */

public class TestUtils {

	public static final String TAG = TestUtils.class.getSimpleName();

	public static Recipe getRecipeByPosition(final int position) {
		final List<Recipe> recipes = loadRecipes();
		final int minimum_size = position + 1;
		return (recipes.size() >= minimum_size) ? recipes.get(position) : new Recipe();
	}

	private static List<Recipe> loadRecipes() {
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
			Log.e(TAG, "caught an exception while storing the recipes ", e);
		}
		Log.d(TAG, String.format("recipes: %s", recipes));
		return recipes;

	}

}
