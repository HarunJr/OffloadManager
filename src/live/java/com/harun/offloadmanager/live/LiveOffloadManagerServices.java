package com.harun.offloadmanager.live;

import android.content.Intent;
import android.util.Log;

import com.harun.offloadmanager.activities.LoginActivity;
import com.harun.offloadmanager.activities.MainActivity;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.infrastructure.OffloadManagerApplication;
import com.harun.offloadmanager.models.ParentModel;
import com.harun.offloadmanager.models.Transaction;
import com.harun.offloadmanager.models.Vehicle;
import com.harun.offloadmanager.service.TransactionServices;
import com.harun.offloadmanager.service.UserServices;
import com.harun.offloadmanager.service.VehicleServices;
import com.squareup.otto.Subscribe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HARUN on 12/18/2016.
 */

public class LiveOffloadManagerServices extends BaseLiveService {
    //TODO: Use syncAdapter
    public static final String LOG_TAG = LiveOffloadManagerServices.class.getSimpleName();
    private LocalStore localStore = new LocalStore(application);

    public LiveOffloadManagerServices(OffloadManagerApplication application, OffloadWebServices api) {
        super(application, api);
    }

    @Subscribe
    public void getVehicleMessage(final VehicleServices.VehiclesServerRequest request) {
        Log.w(LOG_TAG, "getVehicleMessage " + request.query + "\n todayDate: " + request.todayDate);
        Call<ParentModel> call = null;

        if (request.registration != null) {
            call = api.createVehicle(
                    request.query,
                    request.registration,
                    request.vehicle_model,
                    request.manufacture_year,
                    request.seating_capacity);

        } else {
            //get vehicles from server
            Log.w(LOG_TAG, "getVehicles " + request.query);
            call = api.getVehicles(request.query);

//            if (request._id != 0) {
//                Log.w(LOG_TAG, "getVehicleById " + request.query);
//                call = api.getVehicleById(String.valueOf(request._id), request.query);
//
//            } else {
//                //get vehicles from server
//                Log.w(LOG_TAG, "getVehicles " + request.query);
//                call = api.getVehicles(request.query);
//            }
        }
        call.enqueue(new Callback<ParentModel>() {
            int code;
            @Override
            public void onResponse(Call<ParentModel> call, Response<ParentModel> response) {
                Log.w(LOG_TAG, "onResponse " + response.code() + " request.id " + request._id);
                long rowId = 0;
                String type = null;

//                Log.w(LOG_TAG, "status: " + response.body().status+" type: "+ response.body().type);
                code = response.code();
                switch (code) {
                    case 200:
                        if (request.registration != null) {

                            Log.w(LOG_TAG, "vehicle " + request.registration + " created");
                            application.startActivity(new Intent(application, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    .putExtra("code", code));

                        } else {
                            if (request._id != 0) {
                                Log.w(LOG_TAG, "onResponse getVehicleById " + request._id);
                            } else {
                                Transaction transactions = null;

                                for (Vehicle vehiclesModel : response.body().vehicleInfo) {
                                    Log.w(LOG_TAG, "onResponse vehicleReg: " + vehiclesModel.get_id() + "\n" + vehiclesModel.getRegistration() + "\n signUpDate: " + vehiclesModel.getUpdated_at()
                                            + "\nCollection: " + vehiclesModel.getCollection() + "\nExpense: " + vehiclesModel.getExpense()+ "\nExpense: " + vehiclesModel.getUpdated_at());
                                    for (Transaction transaction : vehiclesModel.getTransactionInfo()) {
                                        Log.w(LOG_TAG, "amount: " + transaction.getAmount() + "\n Reg: " + transaction.getVehicle_id()
                                                + "\n type: " + transaction.getType() + "\n timestamp: " + transaction.getTimestamp()
                                                + "\n getCreated_at: " + transaction.getCreated_at());
                                        rowId = localStore.storeTransactionData(transaction);
                                    }

                                    if (rowId > 0) {
                                        Log.w(LOG_TAG, "vehiclesModel: "
                                                +vehiclesModel.getRegistration()+">>"+ vehiclesModel.getCollection() + ">>" + vehiclesModel.getExpense() + ">>" + vehiclesModel.getLastTransactionDate() );

//                                        vehiclesModel = localStore.getVehicleTransactionTime(vehiclesModel);
                                        localStore.storeVehicleData(vehiclesModel);
//                                        Log.w(LOG_TAG, "vehiclesModelLoop " + vehiclesModel.get_id() + "-" + vehiclesModel.getUpdated_at());

                                        vehiclesModel = localStore.getVehicleDataFromTransactions(vehiclesModel.get_id(), request.todayDate);
                                        if (vehiclesModel != null) {
                                            localStore.storeVehicleData(vehiclesModel);
                                            Log.w(LOG_TAG, "vehiclesModelLoop: "
                                                    +vehiclesModel.getRegistration()+">>"+ vehiclesModel.getCollection() + ">>" + vehiclesModel.getExpense() + ">>" + vehiclesModel.getLastTransactionDate() );
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 400:
                        Log.w(LOG_TAG, "token_not_provided " + code);
                        localStore.setUserLoggedIn(false);

                        application.startActivity(new Intent(application, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        break;
                    case 401:
                        Log.w(LOG_TAG, "token_expired " + code);
                        localStore.setUserLoggedIn(false);

                        application.startActivity(new Intent(application, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        break;
                    case 500:
                        Log.w(LOG_TAG, "code: " + code);
                        localStore.setUserLoggedIn(false);

                        application.startActivity(new Intent(application, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        break;
                }
            }

            @Override
            public void onFailure(Call<ParentModel> call, Throwable t) {
                Log.w(LOG_TAG, "onFailure: " + t.getMessage() + "\n Code: " + code);
            }
        });
    }

    @Subscribe
    public void postTransactionMessage(TransactionServices.TransactionsServerRequest request) {
        Log.w(LOG_TAG, "postTransactionMessage: " + request.query + "," + request.vehicleKey + "," + request.amount + ","
                + request.type + "," + request.description + "," + request.date_time);

        Call<Transaction> call = api.postTransaction(request.query, request.vehicleKey, request.amount, request.description);
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                Log.w(LOG_TAG, "onResponse: Transaction code: " + response.code());
                if (response.isSuccessful()) {
                    application.startActivity(new Intent(application, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                call.cancel();
                t.printStackTrace();
                Log.w(LOG_TAG, "onFailure: " + t.getMessage() + "\n" + call.toString());
            }
        });
    }

    @Subscribe
    public void postUserMessage(final UserServices.UserServerRequest request) {
//        Log.w(LOG_TAG, "postUserMessage: " + request.query + "," + request.userName + "," + request.phoneNo + ","
//                + request.email + "," + request.pin + "," + request.group);
        Log.w(LOG_TAG, "postUserMessage: " + request.query + "," + request.userName + ","
                + request.email + "," + request.pin + ",");

        Call<ParentModel> call = null;

        Log.w(LOG_TAG, "query: " + request.query);
        if (request.query.equals("login")) {
            call = api.loginUser(request.email, request.pin);
//            call = api.loginUser("admin@transit.gemilab.com", "secret");
        } else if (request.query.equals("signup")) {
            call = api.registerUser(request.query, request.userName, request.email, request.phoneNo, request.company, request.pin);

        } else if (request.query.equals(localStore.getToken())) {
            Log.w(LOG_TAG, "name: " + request.userName + "\n" + "phone: " + request.phoneNo + "\n" + "role: " + request.role + "\n" + "token: " + request.query + "\n");
            call = api.addUser(request.query, request.userName, request.phoneNo, request.role);

        } else if (request.query.equals("")) {
            application.startActivity(new Intent(application, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }

        assert call != null;
        call.enqueue(new Callback<ParentModel>() {
            int code;
            @Override
            public void onResponse(Call<ParentModel> call, Response<ParentModel> response) {
                Log.w(LOG_TAG, "onResponse: " + response.code());

                code = response.code();
                switch (code) {
                    case 200:
                        Log.w(LOG_TAG, "code: " + response.code() + "\n token: " + response.body().auth_token);
                        if (request.query.equals("login")) {
                            localStore.storeToken(response.body().auth_token);
//                        localStore.setUserLoggedIn(true);

                            application.startActivity(new Intent(application, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    .putExtra("code", code));

                        } else if (request.query.equals("signup")) {
//                        showDialog("Registration Successful", message, code);
                            application.startActivity(new Intent(application, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                        break;
                    case 401:
                        Log.w(LOG_TAG, "code: " + response.code());
                        application.startActivity(new Intent(application, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

                        break;

                }
            }

            @Override
            public void onFailure(Call<ParentModel> call, Throwable t) {
                call.cancel();
                Log.w(LOG_TAG, "onFailure: " + t.getMessage() + "\n" + call.toString());
            }
        });
    }
}
