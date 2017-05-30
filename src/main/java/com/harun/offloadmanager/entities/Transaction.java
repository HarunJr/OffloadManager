package com.harun.offloadmanager.entities;

/**
 * Created by HARUN on 12/27/2016.
 */

public class Transaction {
    public String vehicleKey;
    public int amount;
    public int type;
    public String description;
    public long dateTime;
    public int sync;

    public Transaction(String vehicleKey, int amount, int type, String description, long dateTime, int sync) {
        this.vehicleKey = vehicleKey;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.dateTime = dateTime;
        this.sync = sync;
    }


    public String getVehicleKey() {
        return vehicleKey;
    }

    public int getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public long getDateTime() {
        return dateTime;
    }

    public int getSync() {
        return sync;
    }
}
