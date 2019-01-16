package com.udacity.recipes.baking.baking.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Asim Qureshi
 */

public class RecipeIngredientsAppWidgetRemoteViewsService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.d(getClass().getSimpleName(), "onGetViewFactory: " + "Service called");
		return new RecipeIngredientsAppWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}
