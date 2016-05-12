package com.leegacy.sooji.africaradio.DataObjects;

/**
 * Created by soo-ji on 16-04-12.
 */
public class User{


    private String firstName;
    private String lastName;
    //private String playListID;

    public User() {}

    public User(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
        //this.playListID = "";
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

//    public String getPlayListID() {
//        return playListID;
//    }
//
//    public void setPlayListID(String playListID) {
//        this.playListID = playListID;
//    }
}
