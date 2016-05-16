package com.example.pcx.mymusicwork.bean;

/**
 * Created by pcx on 2016/5/14.
 */
public class DataBean {
    private int secret;
    private String url_albumpic_big;
    private String url_albumpic_small;
    private String downUrl;
    private int seconds;//歌曲时长
    private String singerName;
    private String songName;
    private String mediaUrl;//在线听歌

    public int getSecret() {
        return secret;
    }

    public void setSecret(int secret) {
        this.secret = secret;
    }

    public String getUrl_albumpic_big() {
        return url_albumpic_big;
    }

    public void setUrl_albumpic_big(String url_albumpic_big) {
        this.url_albumpic_big = url_albumpic_big;
    }

    public String getUrl_albumpic_small() {
        return url_albumpic_small;
    }

    public void setUrl_albumpic_small(String url_albumpic_small) {
        this.url_albumpic_small = url_albumpic_small;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
