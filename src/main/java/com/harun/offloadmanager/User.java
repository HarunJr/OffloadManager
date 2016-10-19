package com.harun.offloadmanager;

/**
 * Created by HARUN on 10/12/2016.
 */

public class User {
    public String name, phoneNo, email, pin;

    public User(String name, String phoneNo, String email, String pin){
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.pin = pin;
    }

    public User(String phoneNo, String pin){
        this.phoneNo = phoneNo;
        this.pin = pin;
    }
}
