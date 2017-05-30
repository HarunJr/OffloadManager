package com.harun.offloadmanager.service;

import android.util.Log;

import com.harun.offloadmanager.models.Transaction;

import java.util.ArrayList;

/**
 * Created by HARUN on 12/27/2016.
 */

public class TransactionServices {
    public static final String LOG_TAG = TransactionServices.class.getSimpleName();

    private TransactionServices(){
        Log.w(LOG_TAG, "TransactionServices bus.post" );

    }

    public static class TransactionsServerRequest {
        public String query;
        public Transaction transaction;

        public int vehicleKey;
        public double amount;
        public String type;
        public String description;
        public long date_time;

        public TransactionsServerRequest(String query, Transaction transactionsModel){
            this.query = query;
            this.transaction = transactionsModel;

            this.vehicleKey = transactionsModel.getVehicle_id();
            this.amount = transactionsModel.getAmount();
            this.type = transactionsModel.getType();
            this.description = transactionsModel.getDescription();
            this.date_time = transactionsModel.getTimestamp();
        }
    }

    public static class SearchTransactionsResponse {
        public ArrayList<Transaction> transactions;
    }

}
