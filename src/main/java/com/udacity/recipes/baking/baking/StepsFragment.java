package com.udacity.recipes.baking.baking;

import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.udacity.recipes.baking.baking.StepsFragment.RecipeStepsRecyclerViewAdapter.StepViewHolder;
import com.udacity.recipes.baking.baking.businessObjects.Ingredient;
import com.udacity.recipes.baking.baking.businessObjects.Recipe;
import com.udacity.recipes.baking.baking.businessObjects.Step;
import com.udacity.recipes.baking.baking.dependencies.StepsAdapterPositionApi;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * to handle Step interaction events.
 */
public class StepsFragment extends Fragment implements BakingConstants {

	private interface RecipeStepsAdapterOnClickHandler {

		void onClick(StepViewHolder viewHolder, Step step);
	}

	public class RecipeStepsRecyclerViewAdapter extends RecyclerView.Adapter<RecipeStepsRecyclerViewAdapter.StepViewHolder> {

		public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

			@BindView(R.id.description)
			TextView mAbbreviatedLongDescriptionView;
			@BindView(R.id.shortDescription)
			TextView mShortDescriptionView;
			@BindView(R.id.stepNumber)
			TextView mStepNumberView;
			@BindView(R.id.stepThumbnail)
			ImageView mStepThumbnailView;

			StepViewHolder(View view) {
				super(view);
				ButterKnife.bind(this, view);
				view.setOnClickListener(this);
			}

			@Override
			public void onClick(View view) {
				stepsAdapterPositionApi.setStepsAdapterPosition(getAdapterPosition());
				Step step = (Step) view.getTag();
				notifyDataSetChanged();
				mClickHandler.onClick(this, step);
			}
		}

		final private RecipeStepsAdapterOnClickHandler mClickHandler;
		private final Step[] mSteps;

		RecipeStepsRecyclerViewAdapter(@NonNull Step[] steps, RecipeStepsAdapterOnClickHandler clickHandler) {
			this.mSteps = steps;
			this.mClickHandler = clickHandler;
		}

		@Override
		public int getItemCount() {
			return mSteps.length;
		}

		@Override
		public void onBindViewHolder(final StepViewHolder holder, int position) {
			final Step step = mSteps[position];
			holder.mShortDescriptionView.setText(step.getShortDescription());
			holder.mAbbreviatedLongDescriptionView.setText(step.getFormattedDescription());
			holder.mStepThumbnailView.setVisibility(step.getThumbnailURL().isEmpty() ? View.GONE : View.VISIBLE);
//			Treating the thumbnails JSON object as images (.png, .jpg), since the project designs for images.
//			Since we can't always expect to consume perfect API, we treat it as an error case.
//			The code falls back and uses the thumbnail as an image.
			Glide.with(getActivity()).load(step.getThumbnailURL()).listener(new RequestListener<Drawable>() {
				@Override
				public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
					Log.e(getClass().getSimpleName(),
							String.format("failed to load url: %s with exception %s", step.getThumbnailURL(), e.getMessage()), e);
					return false;
				}

				@Override
				public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
					holder.mStepNumberView.setVisibility(View.GONE);
					return false;
				}
			}).into(holder.mStepThumbnailView);
			holder.mStepNumberView.setText(Integer.toString(position));
			holder.itemView.setBackground(stepsAdapterPositionApi.getStepsAdapterPosition() == position ? touchSelectorSelected : touchSelector);
			holder.itemView.setTag(step);
		}

		@Override
		public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.step_item, parent, false);
			return new StepViewHolder(view);
		}
	}

	private RecipeStepsRecyclerViewAdapter adapter;
	@BindDrawable(R.drawable.ic_arrow_drop_down)
	Drawable arrowDown;
	@BindView(R.id.arrowImage)
	ImageView arrowImage;
	@BindDrawable(R.drawable.ic_arrow_drop_up)
	Drawable arrowUp;
	@BindColor(R.color.colorPrimaryDark)
	int colorPrimaryDark;
	@BindView(R.id.ingredient_list)
	RecyclerView ingredientListRecyclerView;
	@BindView(R.id.ingredientsLayout)
	View ingredientsLayout;
	private boolean isShowIngredients = false;
	private LinearLayoutManager linearLayoutManager;
	Parcelable mListState;
	private Step[] mSteps;
	private int recipeId;
	@BindView(R.id.directionsRecyclerView)
	RecyclerView recyclerView;
	@Inject
	StepsAdapterPositionApi stepsAdapterPositionApi;
	@BindDrawable(R.drawable.touch_selector)
	Drawable touchSelector;
	@BindDrawable(R.drawable.touch_selector_selected)
	Drawable touchSelectorSelected;
	private Unbinder unbinder;

	public StepsFragment() {
		// Required empty public constructor
	}

	public void highlightStep(@NonNull Step step) {
		for (int i = 0; i < mSteps.length; i++) {
			Step thisStep = mSteps[i];
			if (thisStep.getId() == step.getId()) {
				RecipeStepsRecyclerViewAdapter adapter
						= (RecipeStepsRecyclerViewAdapter) recyclerView.getAdapter();
				stepsAdapterPositionApi.setStepsAdapterPosition(i);
				adapter.notifyDataSetChanged();
				break;
			}
		}

	}

	private Recipe loadRecipe(int recipeId) {
		Realm realm = Realm.getDefaultInstance();
		return realm.where(Recipe.class).equalTo("id", recipeId).findFirst();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final MyApplication application = (MyApplication) getActivity().getApplication();
		application.getApplicationComponent().inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle arguments = getArguments();
		mSteps = stepsAdapterPositionApi.getArrayOfSteps(arguments);
		recipeId = arguments.getInt(RECIPE_ID);
		isShowIngredients = arguments.getBoolean(SHOW_INGREDIENTS, false);

		// Inflate the layout for this fragment
		final View view = inflater.inflate(R.layout.fragment_steps, container, false);
		unbinder = ButterKnife.bind(this, view);
		final Recipe recipe = loadRecipe(recipeId);
		arrowImage.setColorFilter(colorPrimaryDark);
		ingredientListRecyclerView.setVisibility(isShowIngredients ? View.VISIBLE : View.GONE);
		arrowImage.setImageDrawable(isShowIngredients ? arrowUp : arrowDown);
		populateIngredientList(recipe.getIngredients());
		setupRecyclerView(mSteps, recipeId);
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void onResume() {
		super.onResume();
		smoothScrollToTarget();
		if (mListState != null) {
			linearLayoutManager.onRestoreInstanceState(mListState);
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save list state
		mListState = linearLayoutManager.onSaveInstanceState();
		outState.putParcelable(LIST_STATE_KEY, mListState);
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		// Retrieve list state and list/item positions
		if (savedInstanceState != null)
			mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
	}

	private void populateIngredientList(@NonNull final List<Ingredient> ingredients) {
		assert ingredientListRecyclerView != null;
		ingredientListRecyclerView.setAdapter(new IngredientsAdapter(getActivity(), ingredients));
	}

	private void setupRecyclerView(final Step[] steps, final int recipeId) {
		// set a LinearLayoutManager with default vertical orientation
		linearLayoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(linearLayoutManager);
		DividerItemDecoration dividerItemDecoration
				= new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
		recyclerView.addItemDecoration(dividerItemDecoration);
		recyclerView.setLayoutFrozen(false);
		adapter = new RecipeStepsRecyclerViewAdapter(mSteps, new RecipeStepsAdapterOnClickHandler() {
			@Override
			public void onClick(StepViewHolder viewHolder, Step step) {
				if (((IngredientsAndDescriptionListActivityCallback) getActivity()).isTwoPane()) {
					Bundle args = new Bundle();
					args.putParcelable(STEP_EXTRA, step);
					args.putInt(RECIPE_ID, recipeId);
					args.putParcelableArray(ARG_STEPS_ARRAY, steps);
					StepDetailFragment stepDetailFragment = new StepDetailFragment();
					stepDetailFragment.setArguments(args);
					getActivity().getSupportFragmentManager().beginTransaction()
							.replace(R.id.steps_detail_container, stepDetailFragment, STEP_DETAIL_FRAGMENT_TAG)
							.commit();
				} else {
					Intent intent = new Intent(getActivity(), StepDetailActivity.class);
					intent.putExtra(STEP_EXTRA, step);
					intent.putExtra(RECIPE_ID, recipeId);
					intent.putExtra(ARG_STEPS_ARRAY, steps);
					startActivity(intent);
				}
			}
		});
		recyclerView.setAdapter(adapter);
	}

	protected void smoothScrollToTarget() {
		final int target;
		try {
			target = stepsAdapterPositionApi.getStepsAdapterPosition();
		} catch (NumberFormatException e) {
			return;
		}
		if ((target < 0) || (target > adapter.getItemCount() - 1)) {
			return;
		}
		linearLayoutManager.smoothScrollToPosition(recyclerView, null, target);
	}

	@OnClick(R.id.ingredientsLayout)
	public void toggleIngredients() {
		if (ingredientListRecyclerView.getVisibility() == View.VISIBLE) {
			arrowImage.setImageDrawable(arrowDown);
			ingredientListRecyclerView.setVisibility(View.GONE);
		} else {
			arrowImage.setImageDrawable(arrowUp);
			ingredientListRecyclerView.setVisibility(View.VISIBLE);
		}
	}
}
