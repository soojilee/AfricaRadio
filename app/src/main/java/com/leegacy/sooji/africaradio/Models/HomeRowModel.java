package com.leegacy.sooji.africaradio.Models;

/**
 * Created by soo-ji on 16-07-07.
 */
public class HomeRowModel extends RowModel {
    private String audioKey;
    private String userName;
    private String title;
    private String caption;
    private String userID;
    private boolean heartPressed=false;
    private int heartCount;

    public String getAudioKey() {
        return audioKey;
    }

    public void setAudioKey(String audioKey) {
        this.audioKey = audioKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isHeartPressed() {
        return heartPressed;
    }

    public void setHeartPressed(boolean heartPressed) {
        this.heartPressed = heartPressed;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
