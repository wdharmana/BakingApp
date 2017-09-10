package com.wdharmana.bakingapp.ui.step;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
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
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.wdharmana.bakingapp.R;
import com.wdharmana.bakingapp.data.model.Step;
import com.wdharmana.bakingapp.ui.detail.RecipeListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment implements ExoPlayer.EventListener {

    public static final String ARG_DATA = "step_data";

    private Step step;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;

    private String videoUrl;
    private Uri videoUri;

    SimpleExoPlayerView playerView;

    long playbackPosition=0, currentWindow=0;

    boolean PORTRAIT;
    private Integer position, length;

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    @BindView(R.id.recipe_detail)
    TextView tv_description;
    @BindView(R.id.player_container)
    LinearLayout playerContainer;
    @BindView(R.id.btn_prev)
    Button btnPrev;
    @BindView(R.id.btn_next)
    Button btnNext;


    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void checkNav() {
        if(position==0) {
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        } else if (position==length-1) {
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);

        } else {
            btnPrev.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void initPlayer(Uri uri) {
        checkNav();
        videoUrl = step.getVideoURL();
        if(!videoUrl.equals("")&&videoUrl!=null) {

            playerView.setVisibility(View.VISIBLE);
            playerContainer.setVisibility(View.VISIBLE);
            if (player == null) {

                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();

                player = ExoPlayerFactory.newSimpleInstance(
                        getActivity(),
                        trackSelector, loadControl);

                playerView.setPlayer(player);

                player.addListener(this);

                MediaSource mediaSource = buildMediaSource(uri);
                player.prepare(mediaSource, true, false);
                player.setPlayWhenReady(true);
                player.seekTo((int) currentWindow, playbackPosition);

            } else {
                MediaSource mediaSource = buildMediaSource(uri);
                player.prepare(mediaSource, true, false);
                player.setPlayWhenReady(true);
                player.seekTo((int) currentWindow, playbackPosition);
            }


        } else {

            playerView.setVisibility(View.GONE);
            playerContainer.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Ups! Video not available.", Toast.LENGTH_SHORT).show();


        }
    }


    private void hideSystemUI() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        //Use Google's "LeanBack" mode to get fullscreen in landscape
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        if (player != null) {

            player.stop();
            player.release();
            player = null;

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("playback_position", playbackPosition);
        outState.putLong("current_window", currentWindow);
        super.onSaveInstanceState(outState);
    }


    private void fullScreen() {
        hideSystemUI();
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);
        ButterKnife.bind(this,rootView);

        if (getArguments().containsKey(ARG_DATA)) {

            step = new Gson().fromJson(
                    getArguments().getString(ARG_DATA),
                    Step.class
            );



            videoUrl = step.getVideoURL();
            videoUri = Uri.parse(videoUrl);

            position = getArguments().getInt("POSITION");
            length = getArguments().getInt("LENGTH");


        }

        playerView = (SimpleExoPlayerView) rootView.findViewById(R.id.video_view);

        // Show the dummy content as text in a TextView.
        if (step != null) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
               // hideSystemUI();

               fullScreen();

                playerContainer.setLayoutParams(
                       new LinearLayout.LayoutParams(
                               LinearLayout.LayoutParams.MATCH_PARENT,
                               LinearLayout.LayoutParams.MATCH_PARENT)
               );
               tv_description.setVisibility(View.GONE);
            } else {
                tv_description.setVisibility(View.VISIBLE);
                tv_description.setText(step.getDescription());
            }


        }



        if(videoUrl!=null) {
            initMediaSession();
            initPlayer(videoUri);
            //hideSystemUI();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                String data = RecipeListActivity.getStep(position);
                step = new Gson().fromJson(data, Step.class);
                Log.e("datas", data);
                setupNavAction();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position--;
                String data = RecipeListActivity.getStep(position);
                step = new Gson().fromJson(data, Step.class);
                Log.e("datas", data);

                setupNavAction();

            }
        });

        return rootView;
    }

    private void setupNavAction() {
        currentWindow=0; playbackPosition=0;
        initPlayer(Uri.parse(step.getVideoURL()));
        tv_description.setText(step.getDescription());
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    player.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    player.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private void initMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new BakingAppCallback());
        mMediaSession.setActive(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if(mMediaSession!=null) {
            mMediaSession.setActive(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    private class BakingAppCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            player.seekTo(0);
        }
    }

}
