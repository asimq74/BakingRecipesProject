package com.udacity.recipes.baking.baking.dependencies;

import java.util.Map;

/**
 * Provides application scoped functionality for overriding network resource values
 *
 * @author Asim Qureshi
 */
public interface ResourceOverridesApi {

	Map<String, Integer> getRecipeIconOverrideMap();

	Map<String, String> getRecipeImageOverrideMap();
}
