package com.ccmheaven.tube.pub;

import java.util.ArrayList;
import java.util.List;

public class SettingInfo {
    private String appName;
    private String appVer;
    private String btnDownload;
    private String btnYoutube;
    private String btnAudio;
    private String adInterstital;
    private String adType;
    private String fixVideoAd;
    private String btnAd;
    private String hitKeyword;
    private String updateTime;
    private String latestUpdate;
    private String email;
    private String playstoreUrl;
    private List<RecommendInfo> recommend = new ArrayList<RecommendInfo>();

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getAppVer() {
        return appVer;
    }

    public String getAdType() {
		return adType;
	}

	public void setAdType(String adType) {
		this.adType = adType;
	}

	public void setBtnDownload(String btnDownload) {
        this.btnDownload = btnDownload;
    }

    public String getBtnDownload() {
        return btnDownload;
    }

    public void setBtnYoutube(String btnYoutube) {
        this.btnYoutube = btnYoutube;
    }

    public String getBtnYoutube() {
        return btnYoutube;
    }

    public void setBtnAudio(String btnAudio) {
        this.btnAudio = btnAudio;
    }

    public String getBtnAudio() {
        return btnAudio;
    }

    public void setAdInterstital(String adInterstital) {
        this.adInterstital = adInterstital;
    }

    public String getAdInterstital() {
        return adInterstital;
    }

    public void setFixVideoAd(String fixVideoAd) {
        this.fixVideoAd = fixVideoAd;
    }

    public String getFixVideoAd() {
        return fixVideoAd;
    }

    public void setHitKeyword(String hitKeyword) {
        this.hitKeyword = hitKeyword;
    }

    public String getHitKeyword() {
        return hitKeyword;
    }

    public void setBtnAd(String btnAd) {
        this.btnAd = btnAd;
    }

    public String getBtnAd() {
        return btnAd;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setLatestUpdate(String latestUpdate) {
        this.latestUpdate = latestUpdate;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPlaystoreUrl(String playstoreUrl) {
        this.playstoreUrl = playstoreUrl;
    }

    public String getPlaystoreUrl() {
        return playstoreUrl;
    }

    public List<RecommendInfo> getRecommend() {
        return recommend;
    }
}
