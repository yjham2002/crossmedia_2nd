package com.RKclassichaeven.tube.models;

import com.ccmheaven.tube.pub.ListInfo;
import com.pierfrancescosoffritti.youtubeplayer.player.PlayerConstants;

/**
 * Created by HP on 2018-06-30.
 */

public class SyncInfo {

    public static final int STATE_PLAY = 100;
    public static final int STATE_PAUSE = 200;
    public static final int STATE_RELEASE = 300;

    public static final int SCENE_BOTTOM = 10;
    public static final int SCENE_VIEW = 20;
    public static final int SCENE_WINDOW = 30;

    private String videoId;
    private float currentTime;
    private String thumbnail;
    private String title;
    private int currentIndex;
    private String author;
    private int state = STATE_RELEASE;
    private int currentScene = SCENE_BOTTOM;

    {
        this.currentIndex = 0;
        this.title = "Play Music";
        this.author = "";
        this.currentTime = 0;
    }

    public SyncInfo(){}

    public int getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(int currentScene) {
        this.currentScene = currentScene;
    }

    public void setBySong(ListInfo info){
        this.videoId = info.getVideoCode();
        this.currentTime = 0;
        this.thumbnail = info.getImageUrl();
        this.title = info.getVideoName();
        this.author = info.getArtistName();
    }

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
        this.currentIndex = 0;
        this.author = "";
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void setStateByAPIState(int apiState){
        switch (apiState){
            case PlayerConstants.PlayerState.PAUSED: case PlayerConstants.PlayerState.VIDEO_CUED:
                this.state = STATE_PAUSE;
                break;
            case PlayerConstants.PlayerState.PLAYING:
                this.state = STATE_PLAY;
                break;
            case PlayerConstants.PlayerState.BUFFERING:
                this.state = STATE_PAUSE;
                break;
            case PlayerConstants.PlayerState.ENDED:
                this.state = STATE_PAUSE;
                break;
            case PlayerConstants.PlayerState.UNKNOWN:
                release();
                break;
            case PlayerConstants.PlayerState.UNSTARTED:
                this.state = STATE_PAUSE;
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "SyncInfo{" +
                "videoId='" + videoId + '\'' +
                ", currentTime=" + currentTime +
                ", thumbnail='" + thumbnail + '\'' +
                ", title='" + title + '\'' +
                ", currentIndex=" + currentIndex +
                ", author='" + author + '\'' +
                ", state=" + state +
                '}';
    }
}
