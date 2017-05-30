package com.harun.offloadmanager.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HARUN on 4/20/2017.
 */

public class TransactionDate {
    @SerializedName("2017-03-22")
    private List<Transaction> transactionInfo;

    public List<Transaction> getTransactionInfo() {
        return transactionInfo;
    }
}
