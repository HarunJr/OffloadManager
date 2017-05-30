package com.harun.offloadmanager.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HARUN on 10/20/2016.
 */

public class Transaction {

    @SerializedName("id")
    private String _id;

    @SerializedName("company_id")
    private String company_id;

    @SerializedName("vehicle_id")
    private int vehicle_id;

    @SerializedName("amount")
    private double amount;

    @SerializedName("type")
    private String type;

    @SerializedName("description")
    private String description;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;


    public String get_id() {
        return _id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getSync() {
        return sync;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }


    public int sync;
    public String vehicleReg;

    public Transaction(int vehicle_id, double amount, String type, String description, long timestamp) {
        this.vehicle_id = vehicle_id;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    public Transaction(int vehicleKey, double amount, String type, String description, long timestamp, int sync) {
        this.vehicle_id = vehicleKey;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
        this.sync = sync;
    }

//    public Transaction(int vehicleKey, String vehicleReg, double amount, String type, String description, long timestamp) {
//        this.vehicle_id = vehicleKey;
//        this.vehicleReg = vehicleReg;
//        this.amount = amount;
//        this.type = type;
//        this.description = description;
//        this.timestamp = timestamp;
//    }

//    public String getDate() {
//        return date;
//    }

//    public Transaction(String phoneNo, String pin){
//        this.phoneNo = phoneNo;
//        this.pin = pin;
//    }

}
