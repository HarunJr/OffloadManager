package com.harun.offloadmanager.models;

/**
 * Created by HARUN on 10/20/2016.
 */

public class Transaction {
    public String id, vehicleKey, amount, type, description, dateTime;

    public Transaction(String id, String vehicleKey, String amount, String type, String description, String dateTime){
        this.id = id;
        this.vehicleKey = vehicleKey;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.dateTime = dateTime;
    }

//    public Transaction(String phoneNo, String pin){
//        this.phoneNo = phoneNo;
//        this.pin = pin;
//    }

}