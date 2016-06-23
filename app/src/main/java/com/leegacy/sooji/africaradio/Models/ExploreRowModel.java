package com.leegacy.sooji.africaradio.Models;

/**
 * Created by soo-ji on 16-06-02.
 */
public class ExploreRowModel extends RowModel{
    private String userID;
    private String recordingID;
    private String recordingTitle;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRecordingID() {
        return recordingID;
    }

    public void setRecordingID(String recordingID) {
        this.recordingID = recordingID;
    }

    public String getRecordingTitle() {
        return recordingTitle;
    }

    public void setRecordingTitle(String recordingTitle) {
        this.recordingTitle = recordingTitle;
    }
}
