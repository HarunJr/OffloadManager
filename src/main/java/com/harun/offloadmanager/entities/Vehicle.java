package com.harun.offloadmanager.entities;

/**
 * Created by HARUN on 12/19/2016.
 */

public class Vehicle {
    private String vehicleReg;
    private String userKey;
    private String make;
    private String model;
    private String YOM;
    private String passCap;
    private long regDate;

    public Vehicle(String vehicleReg, String userKey, String make, String model, String YOM, String passCap, long regDate) {
        this.vehicleReg = vehicleReg;
        this.userKey = userKey;
        this.make = make;
        this.model = model;
        this.YOM = YOM;
        this.passCap = passCap;
        this.regDate = regDate;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getYOM() {
        return YOM;
    }

    public String getPassCap() {
        return passCap;
    }

    public long getRegDate() {
        return regDate;
    }
}
