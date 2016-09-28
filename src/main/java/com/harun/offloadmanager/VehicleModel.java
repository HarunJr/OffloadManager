package com.harun.offloadmanager;

/**
 * Created by HARUN on 11/9/2015.
 */
public class VehicleModel {
    public int iconId;
    public int vehicleId;
    public String vehicleRegistration;
    public String dateTime;
    private double sumTrx = 0;
    private TransactionsModel transactionsModel;



    public double getSumTrx() {
        return sumTrx;
    }

    public void setSumTrx(double sumTrx) {
        this.sumTrx = sumTrx;
    }


    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public TransactionsModel getTransactionsModel() {
        return transactionsModel;
    }

    public void setTransactionsModel(TransactionsModel transactionsModel) {
        this.transactionsModel = transactionsModel;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setVehicleRegistration(String mVehicleRegistration) {
        this.vehicleRegistration = mVehicleRegistration;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleModel that = (VehicleModel) o;

        return !(vehicleRegistration != null ? !vehicleRegistration.equals(that.vehicleRegistration) : that.vehicleRegistration != null);

    }

    @Override
    public int hashCode() {
        return vehicleRegistration != null ? vehicleRegistration.hashCode() : 0;
    }
}
