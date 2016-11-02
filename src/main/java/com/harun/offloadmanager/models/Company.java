package com.harun.offloadmanager.models;

/**
 * Created by HARUN on 10/25/2016.
 */

public class Company {
    public String name, userKey, phoneNo, email, pin;
    public double collection, expense;

    public Company(String name, String userKey, String phoneNo, String email, String pin){
        this.name = name;
        this.userKey = userKey;
        this.phoneNo = phoneNo;
        this.email = email;
        this.pin = pin;
    }

    public Company(String phoneNo, String pin) {
        this.phoneNo = phoneNo;
        this.pin = pin;
    }

    public Company(double vehiclesTotalCollection, double vehiclesTotalExpense){
        this.collection = vehiclesTotalCollection;
        this.expense = vehiclesTotalCollection;
    }

}
