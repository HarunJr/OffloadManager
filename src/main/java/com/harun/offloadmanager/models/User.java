package com.harun.offloadmanager.models;

/**
 * Created by HARUN on 10/12/2016.
 */

public class User {
    public String name, phoneNo, email, pin, company, role;

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public String getPin() {
        return pin;
    }

    public String getCompany() {
        return company;
    }

    public User(String name, String phoneNo, String email, String company, String pin){
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.company = company;
        this.pin = pin;
    }

    public User(String name, String phoneNo, String email, String pin){
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.pin = pin;
    }

    public User(String name, String phoneNo, String role){
        this.name = name;
        this.phoneNo = phoneNo;
        this.role = role;
    }

    public User(String email, String pin) {
        this.email = email;
        this.pin = pin;
    }

}
