package com.udacity.recipes.baking.baking.dependencies;

import java.util.Arrays;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.udacity.recipes.baking.baking.BakingConstants;
import com.udacity.recipes.baking.baking.businessObjects.Step;

/**
 * Implementation of StepsAdapterPositionApi interface
 *
 * @author Asim Qureshi
 */
public class StepsAdapterPositionApiImpl implements StepsAdapterPositionApi, BakingConstants {

	private static final String PREFS_STEPS_ADAPTER_POSITION = "STEPS_ADAPTER_POSITION";

	private final Context context;

	public StepsAdapterPositionApiImpl(@NonNull Context context) {
		this.context = context;
	}

	@Override
	public Step[] getArrayOfSteps(Bundle arguments) {
		if (arguments != null && arguments.containsKey(ARG_STEPS_ARRAY)) {
			final Parcelable[] parcelableArray = arguments.getParcelableArray(ARG_STEPS_ARRAY);
			return Arrays.copyOf(parcelableArray, parcelableArray.length, Step[].class);
		}
		return new Step[0];
	}

	@Override
	public int getStepsAdapterPosition() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt(PREFS_STEPS_ADAPTER_POSITION, 0);
	}

	@Override
	public void resetStepsAdapterPosition() {
		setStepsAdapterPosition(0);
	}

	@Override
	public void setStepsAdapterPosition(int position) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(PREFS_STEPS_ADAPTER_POSITION, position);
		editor.commit();
	}

}
