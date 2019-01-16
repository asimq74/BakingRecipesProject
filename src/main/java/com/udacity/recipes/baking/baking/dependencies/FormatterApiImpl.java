package com.udacity.recipes.baking.baking.dependencies;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.R;
import com.udacity.recipes.baking.baking.businessObjects.Ingredient;

/**
 * Implementation of FormatterApi interface
 *
 * @author Asim Qureshi
 */
public class FormatterApiImpl implements BakingConstants, FormatterApi {

	private static final NumberFormat INGREDIENT_QUANTITY_FORMAT = new DecimalFormat("##.###");

	private final Context context;

	public FormatterApiImpl(Context context) {
		this.context = context;
	}

	@Override
	public String formatIngredientForDisplay(@NonNull final Ingredient ingredient) {
		StringBuilder sb = new StringBuilder(String.format("%s ", formatQuantity(ingredient.getQuantity())));
		sb.append(formatMeasure(ingredient.getMeasure()));
		sb.append(ingredient.getIngredient());
		return sb.toString();
	}

	private String formatMeasure(@NonNull final String measure) {
		return "UNIT".equalsIgnoreCase(measure) ? "" : String.format("%s ", measure.toLowerCase(Locale.US));
	}

	private String formatQuantity(final double quantity) {
		return INGREDIENT_QUANTITY_FORMAT.format(quantity);
	}

	@Override
	public String formatServings(int servings) {
		return formatServings(context.getString(R.string.servings), servings);
	}

	@Override
	public String formatServings(@NonNull String servingsFormat, int servings) {
		return String.format(servingsFormat, Integer.toString(servings));
	}

}
