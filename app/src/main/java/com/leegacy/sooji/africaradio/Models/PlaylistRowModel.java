package com.leegacy.sooji.africaradio.Models;

/**
 * Created by soo-ji on 16-04-23.
 */
public class PlaylistRowModel extends RowModel{
    private String title;
    private String description;
    private String audioFile;
    private int numHearts;
    private int numComments;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getNumHearts() {
        return numHearts;
    }

    public void setNumHearts(int numHearts) {
        this.numHearts = numHearts;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }
}
