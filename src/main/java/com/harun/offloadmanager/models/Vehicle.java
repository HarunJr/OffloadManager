package com.harun.offloadmanager.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HARUN on 10/20/2016.
 */

public class Vehicle {
    public static final String LOG_TAG = Vehicle.class.getSimpleName();

    @SerializedName("id")
    private int _id;

    @SerializedName("company_id")
    private String company_id;

    @SerializedName("registration")
    private String registration;

//    @SerializedName("vehicle_make")
    private String vehicle_make;

    @SerializedName("vehicle_model")
    private String vehicle_model;

    @SerializedName("seating_capacity")
    private String seating_capacity;

    @SerializedName("manufacture_year")
    private String manufacture_year;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("transactions")
    public List<Transaction> transactionInfo;

    private double collection, expense;
    private long lastTransactionDate;

    public String getRegistration() {
        return registration;
    }

    public int get_id() {
        return _id;
    }
    public String getCompany_id() {
        return company_id;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public String getManufacture_year() {
        return manufacture_year;
    }

    public String getSeating_capacity() {
        return seating_capacity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public double getCollection() {return collection;}

    public double getExpense() {
        return expense;
    }

    public List<Transaction> getTransactionInfo() {
        return transactionInfo;
    }

    public long getLastTransactionDate() {
        return lastTransactionDate;
    }

    //Create new Vehicle
    public Vehicle(String company_id, String registration, String vehicle_make, String vehicle_model, String seating_capacity, String manufacture_year, String created_at){
        this.company_id = company_id;
        this.registration = registration;
        this.vehicle_make = vehicle_make;
        this.vehicle_model = vehicle_model;
        this.seating_capacity = seating_capacity;
        this.manufacture_year = manufacture_year;
        this.created_at = created_at;
    }

    public Vehicle(int vehicleKey, String registration, double collection, double expense, long updatedAt) {
        this._id = vehicleKey;
        this.registration = registration;
        this.collection = collection;
        this.expense = expense;
        this.lastTransactionDate = updatedAt;
    }
}
