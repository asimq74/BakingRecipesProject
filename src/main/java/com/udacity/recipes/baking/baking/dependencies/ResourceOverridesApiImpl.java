package com.udacity.recipes.baking.baking.dependencies;

import java.util.Map;
import java.util.TreeMap;

import com.udacity.recipes.baking.baking.R;

/**
 * Implementation of ResourceOverridesApi
 *
 * @author Asim Qureshi
 */
public class ResourceOverridesApiImpl implements ResourceOverridesApi {

	@Override
	public Map<String, Integer> getRecipeIconOverrideMap() {
		TreeMap<String, Integer> map = new TreeMap<>();
		map.put("Nutella Pie", R.drawable.ic_pie);
		map.put("Yellow Cake", R.drawable.ic_yellow_cake);
		map.put("Brownies", R.drawable.ic_brownie);
		map.put("Cheesecake", R.drawable.ic_cheesecake);
		return map;
	}

	@Override
	public Map<String, String> getRecipeImageOverrideMap() {
		TreeMap<String, String> map = new TreeMap<>();
		map.put("Nutella Pie", "https://www.recipeboy.com/wp-content/uploads/2016/09/No-Bake-Nutella-Pie.jpg");
		map.put("Yellow Cake", "https://addapinch.com/wp-content/blogs.dir/3/files/2014/08/yellow-cake-recipe-DSC_4665.jpg");
		map.put("Brownies", "http://www.inspiredtaste.net/wp-content/uploads/2016/06/Brownies-Recipe-2-1200.jpg");
		map.put("Cheesecake", "https://d2gk7xgygi98cy.cloudfront.net/1820-3-large.jpg");
		return map;
	}

}
