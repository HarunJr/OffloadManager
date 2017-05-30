package com.harun.offloadmanager.live;

import com.harun.offloadmanager.models.ParentModel;
import com.harun.offloadmanager.models.Transaction;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OffloadWebServices {
    public static final String LOG_TAG = OffloadWebServices.class.getSimpleName();

    @FormUrlEncoded
    @POST("authenticate/{parameter}")
    Call<ParentModel> registerUser(@Path("parameter") String requestType,
                                   @Field("name") String name,
                                   @Field("email") String email,
                                   @Field("phone") String phone,
                                   @Field("company") String company,
                                   @Field("password") String pin);

    @FormUrlEncoded
    @POST("authenticate")
    Call<ParentModel> loginUser(@Field("email") String email,
                                @Field("password") String pin);

    @FormUrlEncoded
    @POST("users")
    Call<ParentModel> addUser(@Query("token") String token,
                              @Field("name") String name,
                              @Field("phone") String phone,
                              @Field("role") String role);

    @FormUrlEncoded
    @POST("vehicles")
    Call<ParentModel> createVehicle(@Query("token") String token,
                                     @Field("registration") String registration,
                                     @Field("vehicle_model") String model,
                                     @Field("manufacture_year ") String manufacture_year ,
                                     @Field("seating_capacity") String seating_capacity);

    @GET("vehicles")
    Call<ParentModel> getVehicles(@Query("token") String token);

    @GET("vehicles/{id}")
    Call<ParentModel> getVehicleById(@Path("id") String requestType,@Query("token") String token);

    @FormUrlEncoded
    @POST("transactions")
    Call<Transaction> postTransaction(@Query("token") String token,
                                      @Field("vehicle_id") int vehicleId,
                                      @Field("amount") double amount,
                                      @Field("description") String description);

//    public void getVehicles(Call<List<VehiclesModel>> response);
}
