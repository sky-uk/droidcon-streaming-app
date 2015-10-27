package com.sky.droidcon.streamingapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sky.droidcon.streamingapp.R;

/**
 * Simple video view activity
 */
public class VideoViewActivity extends AppCompatActivity {

    private static final String EXTRA_VIDEO_URI = "videoUri";

    public static Intent getVideoViewIntent(Context context, String videoUri) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra(EXTRA_VIDEO_URI, videoUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        String videoUri = getIntent().getStringExtra(EXTRA_VIDEO_URI);

        // find video view
        VideoView videoView = (VideoView) findViewById(R.id.view_video);

        // set height and width
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        videoView.setMinimumWidth(width);
        videoView.setMinimumHeight(height);

        // setup media controller
        videoView.setMediaController(new MediaController(this));

        // set video stream to play
        videoView.setVideoURI(Uri.parse(videoUri));

        // start playback
        videoView.requestFocus();
        videoView.start();
    }

}
