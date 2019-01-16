package com.udacity.recipes.baking.baking.dependencies;

import android.os.Bundle;

import com.udacity.recipes.baking.baking.businessObjects.Step;

/**
 * Provides application scoped functionality for recipe steps
 *
 * @author Asim Qureshi
 */
public interface StepsAdapterPositionApi {

	Step[] getArrayOfSteps(Bundle arguments);

	int getStepsAdapterPosition();

	void resetStepsAdapterPosition();

	void setStepsAdapterPosition(int position);

}
