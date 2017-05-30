package com.harun.offloadmanager.service;

import com.harun.offloadmanager.models.User;

/**
 * Created by HARUN on 2/8/2017.
 */

public class UserServices {
    public static final String LOG_TAG = UserServices.class.getSimpleName();
    public static class UserServerRequest {
        public String query;
        public User userModel;

        public String userName, email, company, pin, role;
        public String phoneNo;

        public UserServerRequest(String query, User user){
//            Log.w(LOG_TAG, "UserServerRequest" + user.getName()+" "+ user.getEmail()+" "+user.getPin());
            this.query = query;
            this.userModel = user;

            this.userName = user.getName();
            this.phoneNo = user.getPhoneNo();
            this.email = user.getEmail();
            this.company = user.getCompany();
            this.pin = user.getPin();
            this.role = user.getRole();
//            this.group = user.getGroup();
        }
    }
}
