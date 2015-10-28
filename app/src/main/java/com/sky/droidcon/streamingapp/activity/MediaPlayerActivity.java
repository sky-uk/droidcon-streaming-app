package com.sky.droidcon.streamingapp.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sky.droidcon.streamingapp.R;

public class MediaPlayerActivity extends AppCompatActivity {

    private static final String EXTRA_VIDEO_URI = "videoUri";

    public static Intent getMediaPlayerIntent(Context context, String videoUri) {
        Intent intent = new Intent(context, MediaPlayerActivity.class);
        intent.putExtra(EXTRA_VIDEO_URI, videoUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        final String videoUri = getIntent().getStringExtra(EXTRA_VIDEO_URI);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        SurfaceHolder holder = surfaceView.getHolder();

        // create media player object
        final MediaPlayer mp = new MediaPlayer();

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mp.setDisplay(holder);
                try {
                    mp.setDataSource(videoUri);
                    mp.prepare();
                } catch (Exception e) {}
                mp.start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mp.stop();
                mp.release();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
        });

    }
}
