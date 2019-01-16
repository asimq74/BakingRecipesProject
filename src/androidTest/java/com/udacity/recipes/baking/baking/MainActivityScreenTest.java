package com.udacity.recipes.baking.baking;

/**
 *
 * This test demos a user clicking on a GridView item in MenuActivity which opens up the
 * corresponding OrderActivity.
 *
 * This test does not utilize Idling Resources yet. If idling is set in the MenuActivity,
 * then this test will fail. See the IdlingResourcesTest for an identical test that
 * takes into account Idling Resources.
 *
 * Created by Asim Qureshi on 1/18/2018.
 */

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.dependencies.FormatterApi;
import com.udacity.recipes.baking.baking.dependencies.FormatterApiImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

	public static final int RECIPE_POSITION = 1;

	// Convenience helper
	public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
		return new RecyclerViewMatcher(recyclerViewId);
	}

	/**
	 * The ActivityTestRule is a rule provided by Android used for functional testing of a single
	 * activity. The activity that will be tested will be launched before each test that's annotated
	 * with @Test and before methods annotated with @Before. The activity will be terminated after
	 * the test and methods annotated with @After are complete. This rule allows you to directly
	 * access the activity during the test.
	 */
	@Rule
	public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
	private Recipe recipe = new Recipe();

	/**
	 * Clicks on a GridView item and checks it opens up the OrderActivity with the correct details.
	 */
	@Test
	public void clickGridViewItem_OpensItemListActivity() {
//		final MyApplication application = (MyApplication) mActivityTestRule.getActivity().getApplication();
		final FormatterApi formatter = new FormatterApiImpl(mActivityTestRule.getActivity());

		recipe = TestUtils.getRecipeByPosition(RECIPE_POSITION);

		// Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
		// gridview item and clicks it.
		onView(withId(R.id.recipesRecyclerView))
				.perform(RecyclerViewActions.actionOnItemAtPosition(RECIPE_POSITION, click()));

		// Checks that the IngredientsAndDescriptionListActivity opens with the correct recipe name displayed
		onView(withId(R.id.recipeTitle)).check(matches(withText(recipe.getName())));

		// Checks that the IngredientsAndDescriptionListActivity opens with the correct recipe name displayed
		onView(withId(R.id.yieldDescription)).check(matches(withText(formatter.formatServings(recipe.getServings()))));

	}

}
