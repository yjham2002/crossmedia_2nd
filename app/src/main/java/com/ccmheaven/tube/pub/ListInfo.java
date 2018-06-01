package com.ccmheaven.tube.pub;

public class ListInfo {
    private int total;
    private String imageUrl;
    private String videoName;
    private String videoCode;
    private String videoUrl;
    private String videoId;
    private String artistName;
    private String runTime;
    private String views;
    private String pureTitle;
    private String pureArtist;

    public String getPureTitle() {
        return pureTitle;
    }

    public void setPureTitle(String pureTitle) {
        this.pureTitle = pureTitle;
    }

    public String getPureArtist() {
        return pureArtist;
    }

    public void setPureArtist(String pureArtist) {
        this.pureArtist = pureArtist;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videourl) {
        videoUrl = videourl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistname) {
        artistName = artistname;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoid) {
        videoId = videoid;
    }

    public String getVideoCode() {
        return videoCode;
    }

    public void setVideoCode(String download) {
        videoCode = download;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoname) {
        videoName = videoname;
    }

    public void setImageUrl(String imageurl) {
        imageUrl = imageurl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setRuntime(String runtime) {
        runTime = runtime;
    }

    public String getRuntime() {
        return runTime;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getViews() {
        return views;
    }
}
