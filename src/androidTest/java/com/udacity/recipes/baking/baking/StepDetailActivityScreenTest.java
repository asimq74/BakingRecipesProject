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

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.businessObjects.Step;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class StepDetailActivityScreenTest implements BakingConstants {

	public static final int RECIPE_POSITION = 1;
	public static final int STEP_POSITION = 0;

	// Convenience helper
	public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
		return new RecyclerViewMatcher(recyclerViewId);
	}

	private Step step = new Step();
	/**
	 * The ActivityTestRule is a rule provided by Android used for functional testing of a single
	 * activity. The activity that will be tested will be launched before each test that's annotated
	 * with @Test and before methods annotated with @Before. The activity will be terminated after
	 * the test and methods annotated with @After are complete. This rule allows you to directly
	 * access the activity during the test.
	 */
	@Rule
	public ActivityTestRule<StepDetailActivity> mActivityRule =
			new ActivityTestRule<StepDetailActivity>(StepDetailActivity.class) {
				@Override
				protected Intent getActivityIntent() {
					final Recipe recipe = TestUtils.getRecipeByPosition(RECIPE_POSITION);
					step = recipe.getSteps().get(STEP_POSITION);
					final Step[] stepsArray = (Step[]) recipe.getSteps().toArray();
					Context targetContext = InstrumentationRegistry.getInstrumentation()
							.getTargetContext();
					Intent result = new Intent(targetContext, StepDetailActivity.class);
					result.putExtra(STEP_EXTRA, step);
					result.putExtra(RECIPE_ID, recipe.getId());
					result.putExtra(ARG_STEPS_ARRAY, stepsArray);
					return result;
				}
			};

	/**
	 * Checks that the description matches step description
	 */
	@Test
	public void description_matches_step_description() {
		onView((withId(R.id.description))).check(matches(withText(step.getDescription())));
	}

	/**
	 * Checks that the short description matches step short description
	 */
	@Test
	public void short_description_matches_step_short_description() {
		onView((withId(R.id.shortDescription))).check(matches(withText(step.getShortDescription())));
	}

}
