package com.udacity.recipes.baking.baking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.udacity.recipes.baking.baking.widget.RecipeIngredientsAppWidgetProvider;

/**
 * Broadcast receiver that provides a delay in order to handle the widget action view
 *
 * @author Asim Qureshi
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver implements BakingConstants {

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOG_TAG);
		//Acquire the lock
		wl.acquire();

		RecipeIngredientsAppWidgetProvider.sendRefreshBroadcast(context);

		//Release the lock
		wl.release();
	}

}
