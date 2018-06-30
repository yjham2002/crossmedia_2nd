package com.RKclassichaeven.tube.models;

/**
 * Created by HP on 2018-06-30.
 */

public class SyncInfo {

    public static final int STATE_PLAY = 100;
    public static final int STATE_PAUSE = 200;
    public static final int STATE_RELEASE = 300;

    private String videoId;
    private float currentTime;
    private String thumbnail;
    private String title;
    private String author;
    private int state = STATE_RELEASE;

    {
        this.title = "Play Music";
        this.author = "";
        this.currentTime = 0;
    }

    public SyncInfo(){}

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(float currentTime) {
        this.currentTime = currentTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getState() {
        return state;
    }

    public void setPlayState(){
        this.state = STATE_PLAY;
    }

    public void setPauseState(){
        this.state = STATE_PAUSE;
    }

    public void release(){
        this.state = STATE_RELEASE;
        this.currentTime = 0;
        this.title = "Play Music";
        this.author = "";
    }

}
