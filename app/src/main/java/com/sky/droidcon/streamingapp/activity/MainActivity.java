package com.sky.droidcon.streamingapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sky.droidcon.streamingapp.R;
import com.sky.droidcon.streamingapp.Video;
import com.sky.droidcon.streamingapp.VideoSamples;
import com.sky.droidcon.streamingapp.VideosAdapter;

public class MainActivity extends AppCompatActivity implements VideosAdapter.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Setup video list
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_videos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VideosAdapter(VideoSamples.SAMPLE_VIDEOS, this));

    }

    @Override
    public void onItemClicked(Video video) {
        //startActivity(VideoViewActivity.getVideoViewIntent(this, video.getContentUrl()));
        startActivity(MediaPlayerActivity.getMediaPlayerIntent(this, video.getContentUrl()));
    }
}
