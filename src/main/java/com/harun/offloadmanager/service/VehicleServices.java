package com.harun.offloadmanager.service;

import android.util.Log;

import com.harun.offloadmanager.models.Vehicle;

import java.util.ArrayList;

/**
 * Created by HARUN on 12/18/2016.
 */

public class VehicleServices {
    public static final String LOG_TAG = VehicleServices.class.getSimpleName();
    private VehicleServices(){
        Log.w(LOG_TAG, "VehicleServices bus.post" );
    }

    public static class VehiclesServerRequest {
        public String query;
        public String todayDate;

        public int _id;
        public String company_id;
        public String registration;
        public String vehicle_make;
        public String vehicle_model;
        public String seating_capacity;
        public String manufacture_year;
        public String created_at;
        public String updated_at;

        public VehiclesServerRequest(String token, String date){
            this.query = token;
            this.todayDate = date;
            Log.w(LOG_TAG, "SearchVehiclesRequest bus.post" );
        }

        public VehiclesServerRequest(String authToken, Vehicle vehicle) {
            this.query = authToken;

            this._id = vehicle.get_id();
            this.company_id = vehicle.getCompany_id();
            this.registration = vehicle.getRegistration();
            this.vehicle_make = vehicle.getVehicle_make();
            this.vehicle_model = vehicle.getVehicle_model();
            this.seating_capacity = vehicle.getSeating_capacity();
            this.manufacture_year = vehicle.getManufacture_year();
            this.created_at = vehicle.getCreated_at();
            this.updated_at = vehicle.getUpdated_at();
        }
    }

    public static class SearchVehiclesResponse {
        public ArrayList<Vehicle> vehicles;
    }
}
