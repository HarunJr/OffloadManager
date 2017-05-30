package com.harun.offloadmanager.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HARUN on 12/18/2016.
 */

public class ParentModel {
    @SerializedName("token")
    public String auth_token;

    @SerializedName("vehicles")
    public List<Vehicle> vehicleInfo;

//    @SerializedName("server_response")
//    public List<ServerResponse> userInfo;


}
