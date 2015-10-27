package com.sky.droidcon.streamingapp;

public class Video {

    public static final int TYPE_DASH = 0;
    public static final int TYPE_HLS = 1;
    public static final int TYPE_OTHER = 2;

    public Video(String title, String contentUrl, int videoType) {
        this.title = title;
        this.contentUrl = contentUrl;
        this.videoType = videoType;
    }

    private String title;
    private String contentUrl;
    private int videoType;

    public String getTitle() {
        return title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public int getVideoType() {
        return videoType;
    }
}
