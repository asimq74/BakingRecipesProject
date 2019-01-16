package com.udacity.recipes.baking.baking;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.recipes.baking.baking.businessObjects.Step;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * Fragment displaying the step detail and associated video
 */
public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener, BakingConstants {

	/**
	 * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
	 */
	public static class MediaReceiver extends BroadcastReceiver {

		public MediaReceiver() {
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			MediaButtonReceiver.handleIntent(mMediaSession, intent);
		}
	}

	/**
	 * Media Session Callbacks, where all external clients control the player.
	 */
	private class MySessionCallback extends MediaSessionCompat.Callback {

		@Override
		public void onPause() {
			mExoPlayer.setPlayWhenReady(false);
		}

		@Override
		public void onPlay() {
			mExoPlayer.setPlayWhenReady(true);
		}

		@Override
		public void onSkipToPrevious() {
			mExoPlayer.seekTo(0);
		}
	}

	private static MediaSessionCompat mMediaSession;
	private final String PLAY_STATE = "playState";
	private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
	private final String STATE_RESUME_POSITION = "resumePosition";
	private final String STATE_RESUME_WINDOW = "resumeWindow";
	private final String TAG = this.getClass().getSimpleName();
	@BindString(R.string.app_name)
	String appName;
	@BindView(R.id.exo_controller)
	@Nullable
	PlaybackControlView controlView;
	@BindDrawable(R.drawable.ic_fullscreen_expand)
	Drawable fullscreenExpand;
	@BindDrawable(R.drawable.ic_fullscreen_skrink)
	Drawable fullscreenSkrink;
	boolean isPlayWhenReady;
	@BindView(R.id.description)
	TextView mDescriptionView;
	private SimpleExoPlayer mExoPlayer;
	private boolean mExoPlayerFullscreen = false;
	@BindView(R.id.exoplayer)
	@Nullable
	SimpleExoPlayerView mExoPlayerView;
	@BindView(R.id.exo_fullscreen_button)
	@Nullable
	FrameLayout mFullScreenButton;
	private Dialog mFullScreenDialog;
	@BindView(R.id.exo_fullscreen_icon)
	@Nullable
	ImageView mFullScreenIcon;
	private NotificationManager mNotificationManager;
	private long mResumePosition;
	private int mResumeWindow;
	@BindView(R.id.shortDescription)
	TextView mShortDescriptionView;
	private PlaybackStateCompat.Builder mStateBuilder;
	private MediaSource mVideoSource;
	@BindView(R.id.main_media_frame)
	@Nullable
	FrameLayout mainMediaFrame;
	@BindString(R.string.notification_text)
	String notificationText;
	@BindString(R.string.pause)
	String pause;
	@BindString(R.string.play)
	String play;
	@BindString(R.string.playStepInstructionVideo)
	String playStepInstructionVideo;
	@BindView(R.id.recipe_instruction_container)
	@Nullable
	View recipeInstructionContainer;
	@BindString(R.string.restart)
	String restart;
	private Step step;
	private Unbinder unbinder;
	private boolean videoFullScreenByDefault = false;

	public StepDetailFragment() {
	}

	private void closeFullscreenDialog() {
		((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
		mainMediaFrame.addView(mExoPlayerView);
		mExoPlayerFullscreen = false;
		mFullScreenDialog.dismiss();
		mFullScreenIcon.setImageDrawable(fullscreenExpand);
	}

	@Optional
	@OnClick(R.id.exo_fullscreen_button)
	public void enterFullScreen() {
		if (!mExoPlayerFullscreen)
			openFullscreenDialog();
		else
			closeFullscreenDialog();
	}

	private void initExoPlayer(Uri mediaUri) {

		BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
		TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
		TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
		LoadControl loadControl = new DefaultLoadControl();
		mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getActivity()), trackSelector, loadControl);
		mExoPlayerView.setPlayer(mExoPlayer);

		// Set the ExoPlayer.EventListener to this activity.
		mExoPlayer.addListener(this);

		boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

		if (haveResumePosition) {
			mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
		}

		// Prepare the MediaSource.
		String userAgent = Util.getUserAgent(getActivity(), appName);
		mVideoSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
				getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
		mExoPlayerView.getPlayer().prepare(mVideoSource);
		mExoPlayer.setPlayWhenReady(isPlayWhenReady);
	}

	private void initFullscreenDialog() {

		mFullScreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
			public void onBackPressed() {
				if (mExoPlayerFullscreen)
					closeFullscreenDialog();
				super.onBackPressed();
			}
		};
	}

	/**
	 * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
	 * and media controller.
	 */
	private void initializeMediaSession() {

		// Create a MediaSessionCompat.
		mMediaSession = new MediaSessionCompat(getActivity(), TAG);

		// Enable callbacks from MediaButtons and TransportControls.
		mMediaSession.setFlags(
				MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
						MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

		// Do not let MediaButtons restart the player when the app is not visible.
		mMediaSession.setMediaButtonReceiver(null);

		// Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
		mStateBuilder = new PlaybackStateCompat.Builder()
				.setActions(
						PlaybackStateCompat.ACTION_PLAY |
								PlaybackStateCompat.ACTION_PAUSE |
								PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
								PlaybackStateCompat.ACTION_PLAY_PAUSE);

		mMediaSession.setPlaybackState(mStateBuilder.build());

		// MySessionCallback has methods that handle callbacks from a media controller.
		mMediaSession.setCallback(new MySessionCallback());

		// Start the Media Session since the activity is active.
		mMediaSession.setActive(true);

	}

	protected boolean isVideoUrlEmpty() {
		return step.getVideoURL().isEmpty();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle arguments = getArguments();
		if (arguments != null) {
			step = arguments.getParcelable(STEP_EXTRA);
		}
		if (savedInstanceState != null) {
			mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
			mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
			mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
			isPlayWhenReady = savedInstanceState.getBoolean(PLAY_STATE);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		// Initialize the player view.
		final View view = inflater.inflate(isVideoUrlEmpty() ? R.layout.fragment_step_instruction_only : R.layout.fragment_step_detail, container, false);
		unbinder = ButterKnife.bind(this, view);
		videoFullScreenByDefault = getResources().getBoolean(R.bool.videoFullScreen);
		mDescriptionView.setText(step.getDescription());
		mShortDescriptionView.setText(step.getShortDescription());
		recipeInstructionContainer.setVisibility(videoFullScreenByDefault ? View.GONE : View.VISIBLE);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isVideoUrlEmpty()) {
			return;
		}
		if (mExoPlayerFullscreen) {
			mExoPlayerFullscreen = false;
			if (mFullScreenDialog != null) {
				mFullScreenDialog.dismiss();
			}
		}
		releasePlayer();
	}

	// When binding a fragment in onCreateView, set the views to null in onDestroyView.
	// ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	@Override
	public void onLoadingChanged(boolean isLoading) {

	}

	@Override
	public void onPause() {
		super.onPause();
		if (isVideoUrlEmpty()) {
			return;
		}
		if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {
			mResumeWindow = mExoPlayerView.getPlayer().getCurrentWindowIndex();
			mResumePosition = Math.max(0, mExoPlayerView.getPlayer().getContentPosition());
			mExoPlayerView.getPlayer().release();
		}

		if (mFullScreenDialog != null)
			mFullScreenDialog.dismiss();
	}

	@Override
	public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

	}

	@Override
	public void onPlayerError(ExoPlaybackException error) {
		Log.e(TAG, "error during playback - ", error);
		mExoPlayerView.setVisibility(View.GONE);
	}

	/**
	 * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
	 * PlayBackState to keep in sync, and post the media notification.
	 * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
	 * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
	 *                      STATE_BUFFERING, or STATE_ENDED.
	 */
	@Override
	public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
		if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
			mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
					mExoPlayer.getCurrentPosition(), 1f);
		} else if ((playbackState == ExoPlayer.STATE_READY)) {
			mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
					mExoPlayer.getCurrentPosition(), 1f);
		}
		mMediaSession.setPlaybackState(mStateBuilder.build());
		showNotification(mStateBuilder.build());
	}

	@Override
	public void onPositionDiscontinuity() {

	}

	@Override
	public void onRepeatModeChanged(int repeatMode) {

	}

	@Override
	public void onResume() {
		super.onResume();
		if (isVideoUrlEmpty()) {
			recipeInstructionContainer.setVisibility(View.VISIBLE);
			return;
		}
		initFullscreenDialog();
		String userAgent = Util.getUserAgent(getActivity(), getActivity().getApplicationContext().getApplicationInfo().packageName);
		final Uri videoUri = Uri.parse(step.getVideoURL());
		mVideoSource = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
				getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
		initializeMediaSession();
		initExoPlayer(videoUri);

		if (mExoPlayerFullscreen) {
			((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
			mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			mFullScreenIcon.setImageDrawable(fullscreenSkrink);
			mFullScreenDialog.show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
		outState.putLong(STATE_RESUME_POSITION, mResumePosition);
		outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
		if (null != mExoPlayer) {
			isPlayWhenReady = mExoPlayer.getPlayWhenReady();
		}
		outState.putBoolean(PLAY_STATE, isPlayWhenReady);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onTimelineChanged(Timeline timeline, Object manifest) {

	}

	@Override
	public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

	}

	private void openFullscreenDialog() {

		((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
		mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mFullScreenIcon.setImageDrawable(fullscreenSkrink);
		mExoPlayerFullscreen = true;
		mFullScreenDialog.show();
	}

	/**
	 * Release ExoPlayer.
	 */
	private void releasePlayer() {
		if (mNotificationManager != null) {
			mNotificationManager.cancelAll();
		}
		if (mExoPlayer != null) {
			mExoPlayer.stop();
			mExoPlayer.release();
			mExoPlayer = null;
			mMediaSession.setActive(false);
		}
	}

	/**
	 * Shows Media Style notification, with actions that depend on the current MediaSession
	 * PlaybackState.
	 * @param state The PlaybackState of the MediaSession.
	 */
	private void showNotification(PlaybackStateCompat state) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "14");

		int icon;
		String play_pause;
		if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
			icon = R.drawable.exo_controls_pause;
			play_pause = pause;
		} else {
			icon = R.drawable.exo_controls_play;
			play_pause = play;
		}

		NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
				icon, play_pause,
				MediaButtonReceiver.buildMediaButtonPendingIntent(getActivity(),
						PlaybackStateCompat.ACTION_PLAY_PAUSE));

		NotificationCompat.Action restartAction = new NotificationCompat
				.Action(R.drawable.exo_controls_previous, restart,
				MediaButtonReceiver.buildMediaButtonPendingIntent
						(getActivity(), PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

		PendingIntent contentPendingIntent = PendingIntent.getActivity
				(getActivity(), 0, new Intent(getActivity(), StepDetailActivity.class), 0);

		builder.setContentTitle(playStepInstructionVideo)
				.setContentText(notificationText)
				.setContentIntent(contentPendingIntent)
				.setSmallIcon(R.drawable.ic_music_note)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.addAction(restartAction)
				.addAction(playPauseAction);

		mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, builder.build());
	}

}
