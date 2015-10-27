package com.sky.droidcon.streamingapp;

public class VideoSamples {

    private VideoSamples() {
    }

    public static final Video[] SAMPLE_VIDEOS = new Video[]{
            new Video("HLS - Bip Bop", "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8", Video.TYPE_HLS),

            new Video("MP4 - Sky news - James Bond", "http://video.news.sky.com/video/h264/vod/700/2015/10/DIGMODANIELCRAIGINTV1730151026175910521445882398763700.mp4", Video.TYPE_OTHER),

            //https://www.youtube.com/watch?v=y60wDzZt8yg
            new Video("HLS - Sky news Live", BuildConfig.SKY_NEWS_STREAM, Video.TYPE_HLS),

            new Video("DASH - Google Glass", "http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?"
                    + "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&ip=0.0.0.0&"
                    + "ipbits=0&expire=19000000000&signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7."
                    + "8506521BFC350652163895D4C26DEE124209AA9E&key=ik0", Video.TYPE_DASH),

            // https://www.youtube.com/watch?v=Ge4CYjgb5No
            new Video("DASH - Sky Store - Idris", BuildConfig.SKY_STORE_STREAM, Video.TYPE_DASH)
    };
}