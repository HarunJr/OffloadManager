package com.harun.offloadmanager.models;

/**
 * Created by HARUN on 10/20/2016.
 */

public class Vehicle {
    public String registration, userKey, make, model, YOM, passCap, regDate, transDate;
    public String collection, expense;

    public Vehicle(String registration, String userKey, String make, String model, String YOM, String passCap, String date){
        this.registration = registration;
        this.userKey = userKey;
        this.make = make;
        this.model = model;
        this.YOM = YOM;
        this.passCap = passCap;
        this.regDate = date;
    }

    public Vehicle(String registration, String regDate, String totalDayCollection, String totalDayExpense, String dateTime){
        this.registration = registration;
        this.collection = totalDayCollection;
        this.expense = totalDayExpense;
        this.transDate = dateTime;
        this.regDate = regDate;
    }
}
