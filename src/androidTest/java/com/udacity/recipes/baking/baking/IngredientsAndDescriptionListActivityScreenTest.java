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

import java.util.List;

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
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class IngredientsAndDescriptionListActivityScreenTest implements BakingConstants {

	public static final int INGREDIENT_POSITION = 3;
	public static final int RECIPE_POSITION = 1;
	public static final int STEP_POSITION = 3;

	// Convenience helper
	public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
		return new RecyclerViewMatcher(recyclerViewId);
	}

	private Recipe recipe = new Recipe();
	/**
	 * The ActivityTestRule is a rule provided by Android used for functional testing of a single
	 * activity. The activity that will be tested will be launched before each test that's annotated
	 * with @Test and before methods annotated with @Before. The activity will be terminated after
	 * the test and methods annotated with @After are complete. This rule allows you to directly
	 * access the activity during the test.
	 */
	@Rule
	public ActivityTestRule<IngredientsAndDescriptionListActivity> mActivityRule =
			new ActivityTestRule<IngredientsAndDescriptionListActivity>(IngredientsAndDescriptionListActivity.class) {
				@Override
				protected Intent getActivityIntent() {
					recipe = TestUtils.getRecipeByPosition(RECIPE_POSITION);
					Context targetContext = InstrumentationRegistry.getInstrumentation()
							.getTargetContext();
					Intent result = new Intent(targetContext, IngredientsAndDescriptionListActivity.class);
					result.putExtra(RECIPE_ID, recipe.getId());
					return result;
				}
			};

	/**
	 * Check that the indexed step contains the correct short description
	 */
	@Test
	public void step_descendant_contains_shortDescription() {
		final List<Step> steps = recipe.getSteps();
		if (steps.isEmpty()) return;
		final Step step = steps.get(STEP_POSITION);
		onView(withRecyclerView(R.id.directionsRecyclerView).atPosition(STEP_POSITION))
				.check(matches(hasDescendant(withText(step.getShortDescription()))));
	}

}
