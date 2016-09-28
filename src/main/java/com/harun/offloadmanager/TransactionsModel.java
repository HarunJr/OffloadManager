package com.harun.offloadmanager;

/**
 * Created by HARUN on 11/11/2015.
 */
public class TransactionsModel {
    public int getVehicleKey() {
        return vehicleKey;
    }

    public void setVehicleKey(int vehicleKey) {
        this.vehicleKey = vehicleKey;
    }

    public int vehicleKey;
    public int type;
    public String description;
    public String dateTime;
    public Double amount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
