package com.udacity.recipes.baking.baking.businessObjects;

import java.util.Arrays;
import java.util.List;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.udacity.recipes.baking.baking.R;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Represents a Recipe
 *
 * @author Asim Qureshi
 */
public class Recipe extends RealmObject {

	@SerializedName(value = "iconResource")
	private Integer iconResource = R.drawable.ic_yellow_cake;
	@SerializedName(value = "id")
	@PrimaryKey
	private Integer id;
	@SerializedName(value = "image")
	private String image;
	@SerializedName(value = "ingredients")
	private RealmList<Ingredient> ingredients = new RealmList<>();
	@SerializedName(value = "name")
	private String name;
	@SerializedName(value = "servings")
	private Integer servings;
	@SerializedName(value = "steps")
	private RealmList<Step> steps = new RealmList<>();

	public Recipe() {
	}

	public Integer getIconResource() {
		return iconResource;
	}

	public Integer getId() {
		return id;
	}

	public String getImage() {
		return image;
	}

	@NonNull
	public List<Ingredient> getIngredients() {
		return Arrays.asList(ingredients.toArray(new Ingredient[ingredients.size()]));
	}

	public String getName() {
		return name;
	}

	public Integer getServings() {
		return servings;
	}

	public List<Step> getSteps() {
		return Arrays.asList(steps.toArray(new Step[steps.size()]));
	}

	public void setIconResource(Integer iconResource) {
		this.iconResource = iconResource;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setIngredients(RealmList<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setServings(Integer servings) {
		this.servings = servings;
	}

	public void setSteps(RealmList<Step> steps) {
		this.steps = steps;
	}

	@Override
	public String toString() {
		return "Recipe{" +
				"id=" + id +
				", image='" + image + '\'' +
				", ingredients=" + getIngredients() +
				", name='" + name + '\'' +
				", servings=" + servings +
				", iconResource=" + iconResource +
				", steps=" + getSteps() +
				'}';
	}
}
