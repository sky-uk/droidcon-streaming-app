/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.droidcon.streamingapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.MediaController;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;

import com.sky.droidcon.streamingapp.R;
import com.sky.droidcon.streamingapp.Video;
import com.sky.droidcon.streamingapp.exoplayer.DashRendererBuilder;
import com.sky.droidcon.streamingapp.exoplayer.DemoPlayer;
import com.sky.droidcon.streamingapp.exoplayer.ExtractorRendererBuilder;
import com.sky.droidcon.streamingapp.exoplayer.HlsRendererBuilder;

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
public class ExoPlayerActivity extends Activity implements SurfaceHolder.Callback, OnClickListener,
        DemoPlayer.Listener,
        AudioCapabilitiesReceiver.Listener {

    // For use within demo app code.
    public static final String CONTENT_TYPE_EXTRA = "content_type";

    private MediaController mediaController;
    private View shutterView;
    private AspectRatioFrameLayout videoFrame;
    private SurfaceView surfaceView;
    private Button retryButton;

    private DemoPlayer player;
    private boolean playerNeedsPrepare;

    private long playerPosition;

    private Uri contentUri;
    private int contentType;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;

    public static Intent getPlayerIntent(Context context, String videoUri, int videoType) {
        return new Intent(context, ExoPlayerActivity.class)
                .setData(Uri.parse(videoUri))
                .putExtra(ExoPlayerActivity.CONTENT_TYPE_EXTRA, videoType);
    }

    // Activity lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);
        View root = findViewById(R.id.root);
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    toggleControlsVisibility();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.performClick();
                }
                return true;
            }
        });
        root.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE
                        || keyCode == KeyEvent.KEYCODE_MENU) {
                    return false;
                }
                return mediaController.dispatchKeyEvent(event);
            }
        });

        shutterView = findViewById(R.id.shutter);

        videoFrame = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        // add callback to surface view
        surfaceView.getHolder().addCallback(this);

        // Could swap this out with our own player controls
        mediaController = new MediaController(this);
        mediaController.setAnchorView(root);

        retryButton = (Button) findViewById(R.id.retry_button);
        retryButton.setOnClickListener(this);

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(this, this);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        playerPosition = 0;
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        contentUri = intent.getData();
        contentType = intent.getIntExtra(CONTENT_TYPE_EXTRA, Video.TYPE_OTHER);
        if (player == null) {
            preparePlayer(true);
        } else {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        shutterView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        if (view == retryButton) {
            preparePlayer(true);
        }
    }

    // AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        player.setBackgrounded(backgrounded);
    }

    // Internal methods

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
        switch (contentType) {
            case Video.TYPE_DASH:
                return new DashRendererBuilder(this, userAgent, contentUri.toString(), null);
            case Video.TYPE_HLS:
                return new HlsRendererBuilder(this, userAgent, contentUri.toString());
            case Video.TYPE_OTHER:
                return new ExtractorRendererBuilder(this, userAgent, contentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
            updateButtonVisibilities();
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }
        updateButtonVisibilities();
    }

    @Override
    public void onError(Exception e) {
        playerNeedsPrepare = true;
        updateButtonVisibilities();
        showControls();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        shutterView.setVisibility(View.GONE);
        videoFrame.setAspectRatio(height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    // User controls

    private void updateButtonVisibilities() {
        retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
    }

    private void toggleControlsVisibility() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        } else {
            showControls();
        }
    }

    private void showControls() {
        mediaController.show(0);
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

}