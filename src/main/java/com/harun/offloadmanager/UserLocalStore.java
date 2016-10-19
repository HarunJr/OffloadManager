package com.harun.offloadmanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HARUN on 10/12/2016.
 */

public class UserLocalStore {
    private static final String SP_NAME = "userDetails";
    private SharedPreferences userLocal_SP;

    public UserLocalStore(Context context){
        userLocal_SP = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putString("name", user.name);
        spEditor.putString("phoneNo", user.phoneNo);
        spEditor.putString("email", user.email);
        spEditor.putString("pin", user.pin);
        spEditor.apply();
    }

    public User getLoggedInUser(){
        String name = userLocal_SP.getString("name", "");
        String phoneNo = userLocal_SP.getString("phoneNo", "");
        String email = userLocal_SP.getString("email", "");
        String pin = userLocal_SP.getString("pin", "");

        return new User(name, phoneNo, email, pin);
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor= userLocal_SP.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.apply();
    }

    public boolean getUserLoggedIn(){
        if (userLocal_SP.getBoolean("loggedIn", false)){
            return true;
        }else {
            return false;
        }
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor= userLocal_SP.edit();
        spEditor.clear();
        spEditor.apply();
    }
}
