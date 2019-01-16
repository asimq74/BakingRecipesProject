package com.udacity.recipes.baking.baking;

import android.app.Application;
import android.content.Context;

import com.udacity.recipes.baking.baking.components.ApplicationComponent;
import com.udacity.recipes.baking.baking.components.DaggerApplicationComponent;
import com.udacity.recipes.baking.baking.modules.ApplicationModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Base Application Class
 */

public class MyApplication extends Application {

	public static MyApplication get(Context context) {
		return (MyApplication) context.getApplicationContext();
	}

	private ApplicationComponent applicationComponent;

	public ApplicationComponent getApplicationComponent() {
		return applicationComponent;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		applicationComponent = DaggerApplicationComponent
				.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
		applicationComponent.inject(this);

		Realm.init(this);
		RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();
		Realm.setDefaultConfiguration(config);

	}

}
