package com.sky.droidcon.streamingapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private final Video[] videos;
    private final VideosAdapter.Listener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mContainer;
        TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mContainer = view;
            mTextView = (TextView) view.findViewById(R.id.txt_title);
        }
    }

    public VideosAdapter(Video[] videos, VideosAdapter.Listener listener) {
        this.videos = videos;
        this.listener = listener;
    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Video video = videos[position];

        holder.mTextView.setText(video.getTitle());
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(video);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videos.length;
    }

    public interface Listener {
        void onItemClicked(Video video);
    }
}