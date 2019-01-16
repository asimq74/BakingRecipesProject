package com.udacity.recipes.baking.baking.businessObjects;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Represents an Ingredient
 *
 * @author Asim Qureshi
 */
public class Ingredient extends RealmObject {

	@SerializedName(value = "ingredient")
	private String ingredient;
	@SerializedName(value = "measure")
	private String measure;
	@SerializedName(value = "quantity")
	private Double quantity;

	public Ingredient() {
	}

	public String getIngredient() {
		return ingredient;
	}

	public String getMeasure() {
		return measure;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Ingredient{" +
				"ingredient='" + ingredient + '\'' +
				", measure='" + measure + '\'' +
				", quantity=" + quantity +
				'}';
	}
}