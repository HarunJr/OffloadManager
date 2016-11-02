/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harun.offloadmanager.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;

import static com.harun.offloadmanager.data.OffloadContract.CONTENT_AUTHORITY;
import static com.harun.offloadmanager.data.OffloadContract.PATH_TRANSACTIONS;
import static com.harun.offloadmanager.data.OffloadContract.PATH_VEHICLE;
import static com.harun.offloadmanager.data.OffloadContract.TransactionEntry;
import static com.harun.offloadmanager.data.OffloadContract.VehicleEntry;

public class OffloadProvider extends ContentProvider {
    private static final String LOG_TAG = OffloadProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private OffloadDbHelper mOpenHelper;

    static final int VEHICLES = 100;
    static final int VEHICLE_WITH_START_DATE = 101;

    static final int TRANSACTIONS = 200;
    static final int TRANSACTION_WITH_ID = 201;
    static final int TRANSACTIONS_WITH_VEHICLE_REGISTRATION = 202;
    static final int TRANSACTION_WITH_START_DATE = 203;
    static final int TRANSACTION_WITH_VEHICLE_AND_DATE = 204;
    static final int TRANSACTION_WITH_VEHICLE_AND_START_DATE2 = 205;

    private static final SQLiteQueryBuilder sTransactionByVehicleQueryBuilder;

    static {
        sTransactionByVehicleQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //transaction INNER JOIN vehicle ON transaction.vehicle_id = vehicle._id
        sTransactionByVehicleQueryBuilder.setTables(TransactionEntry.TABLE_NAME
                + " INNER JOIN " +
                VehicleEntry.TABLE_NAME
                + " ON "
                + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_VEHICLE_KEY +
                " = " + VehicleEntry.TABLE_NAME + "." + VehicleEntry.COLUMN_VEHICLE_REGISTRATION);
    }

//    private static final SQLiteQueryBuilder sTransactionByOffloadQueryBuilder;
//
//    static {
//        sTransactionByOffloadQueryBuilder = new SQLiteQueryBuilder();
//
//        //This is an inner join which looks like
//        //transaction INNER JOIN vehicle ON transaction.vehicle_id = vehicle._id
//        sTransactionByOffloadQueryBuilder.setTables(TransactionEntry.TABLE_NAME
//                + " INNER JOIN " +
//                VehicleEntry.TABLE_NAME
//                + " ON "
//                + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_VEHICLE_KEY +
//                " = " + VehicleEntry.TABLE_NAME + "." + VehicleEntry.COLUMN_VEHICLE_REGISTRATION);
//    }

    private static final SQLiteQueryBuilder sVehicleWithTransactionQueryBuilder;

    static {
        sVehicleWithTransactionQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //transaction INNER JOIN vehicle ON transaction.vehicle_id = vehicle._id
        sVehicleWithTransactionQueryBuilder.setTables(
                VehicleEntry.TABLE_NAME
                        + " LEFT OUTER JOIN " +
                        TransactionEntry.TABLE_NAME
                        + " ON "
                        + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_VEHICLE_KEY +
                        " = " + VehicleEntry.TABLE_NAME + "." + VehicleEntry.COLUMN_VEHICLE_REGISTRATION);
    }

    private static final String sVehicleWithDateSelection =
            VehicleEntry.TABLE_NAME +
                    "." + VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME + " >= ? AND "+
                    VehicleEntry.TABLE_NAME+"."+VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME+" < ?";

    private static final String sVehiclesWithHistoryDateSelection =
            "strftime('%Y-%m-%d', "
                    +TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_DATE_TIME
                    +"/ 1000, 'unixepoch') >= strftime('%Y-%m-%d', ?/ 1000, 'unixepoch')  AND " +
                    "strftime('%Y-%m-%d', "
                    +TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_DATE_TIME
                    + "/ 1000, 'unixepoch')< strftime('%Y-%m-%d', ?/ 1000, 'unixepoch') AND "
                    + TransactionEntry.TABLE_NAME +"." + TransactionEntry.COLUMN_TYPE +" = ?";

    private static final String sTransactionWithTransactionId =
            TransactionEntry.TABLE_NAME +
                    "." + TransactionEntry.COLUMN_TRANSACTION_ID + " = ? ";

    private static final String sTransactionWithStartDateSelection =
            TransactionEntry.TABLE_NAME +
                    "." + TransactionEntry.COLUMN_DATE_TIME + " >= ? ";

    private static final String sTransactionWithVehicleId =
            TransactionEntry.TABLE_NAME +
                    "." + TransactionEntry.COLUMN_VEHICLE_KEY + " = ? ";

    static final String sTransactionsWithVehicleIdAndStartDateSelection =
            TransactionEntry.TABLE_NAME
                    + "."
                    + TransactionEntry.COLUMN_VEHICLE_KEY + " = ? AND "
                    + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_DATE_TIME + " >= ? ";

    static final String sTransactionWithVehicleIdAndTransactionId =
            TransactionEntry.TABLE_NAME +
                    "."
                    + TransactionEntry.COLUMN_VEHICLE_KEY + " = ? AND "
                    + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_TRANSACTION_ID + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sVehicleRegistrationWithStartDateSelection =
            TransactionEntry.TABLE_NAME +
                    "." + TransactionEntry.COLUMN_VEHICLE_KEY + " = ? AND " +
                    "strftime('%Y-%m-%d', "+TransactionEntry.TABLE_NAME + "."
                    + TransactionEntry.COLUMN_DATE_TIME + "/ 1000, 'unixepoch') = strftime('%Y-%m-%d', ?/ 1000, 'unixepoch') ";

    //location.location_setting = ? AND date = ?
    private static final String sVehicleRegistrationAndDaySelection =
            VehicleEntry.TABLE_NAME +
                    "." + VehicleEntry.COLUMN_VEHICLE_REGISTRATION + " = ? AND "
                    + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_DATE_TIME + " = ? ";


    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PATH_VEHICLE, VEHICLES);
        matcher.addURI(authority, PATH_VEHICLE + "/#/#", VEHICLE_WITH_START_DATE);

        matcher.addURI(authority, PATH_TRANSACTIONS, TRANSACTIONS);
        matcher.addURI(authority, PATH_TRANSACTIONS + "/#", TRANSACTION_WITH_ID);
        matcher.addURI(authority, PATH_TRANSACTIONS + "/*", TRANSACTIONS_WITH_VEHICLE_REGISTRATION);
        matcher.addURI(authority, PATH_TRANSACTIONS + "date/*", TRANSACTION_WITH_START_DATE);
        matcher.addURI(authority, PATH_TRANSACTIONS + "/#/#/#", TRANSACTION_WITH_VEHICLE_AND_DATE);
        matcher.addURI(authority, PATH_TRANSACTIONS + "/*/#", TRANSACTION_WITH_VEHICLE_AND_START_DATE2);

        return matcher;

    }

    private Cursor getVehicles(String[] projection, String sortOrder) {

        Log.d(LOG_TAG, "ALL_VEHICLES_CODE");
        return mOpenHelper.getReadableDatabase().query(
                VehicleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder

        );
    }



    //Retrieve all Vehicle items created beyond the start date given
    private Cursor getVehiclesByStartDate(Uri uri, String[] projection, String sortOrder) {

//        SELECT * FROM vehicle
//        WHERE last_transaction_date_time BETWEEN 1473428299965
//        AND 1473514699965

//        SELECT vehicle.vehicleRegistration, SUM(transactions.amount), transactions.transaction_date_time
//        FROM vehicle
//        LEFT JOIN transactions
//        ON transactions.vehicle_id = vehicle.vehicleRegistration
//        WHERE strftime('%Y-%m-%d', transactions.transaction_date_time/ 1000, 'unixepoch') >= date('2016-09-12')
//        AND strftime('%Y-%m-%d', transactions.transaction_date_time/ 1000, 'unixepoch') < date('2016-09-13') AND
//        transactions.type = 1
//        GROUP BY vehicleRegistration


//        select * from transactions where strftime('%Y-%m-%d', transactions.transaction_date_time/ 1000, 'unixepoch') >=
//                date('2016-09-12') AND strftime('%Y-%m-%d', transactions.transaction_date_time/ 1000, 'unixepoch') < date('2016-09-13')

        long calDate = VehicleEntry.getCalDateFromUri(uri);
        long nextDate = VehicleEntry.getNextDateFromUri(uri);

        String selection = sVehicleWithDateSelection;
        String[] selectionArgs = new String[]{String.valueOf(calDate), String.valueOf(nextDate)};
        String groupBy = "vehicleRegistration";

        Log.d(LOG_TAG, "ALL_VEHICLES_WITH_DATE_CODE: " + selection +" : "+ Arrays.toString(selectionArgs));
//        return mOpenHelper.getReadableDatabase().query(
//                VehicleEntry.TABLE_NAME,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                sortOrder
//        );

        return sTransactionByVehicleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                groupBy,
                null,
                sortOrder
        );
    }

    //Retrieve all Vehicle items created beyond the start date given
    private Cursor getVehiclesAndTransactionByStartDate(Uri uri, String[] projection, String sortOrder) {
        long calDate = TransactionEntry.getCalDateFromTransactionsUri(uri);
        long nextDate = TransactionEntry.getNextDateFromTransactionsUri(uri);
        int type = TransactionEntry.getTypeFromUri(uri);
        Log.d(LOG_TAG, "Type ==: "+ type);

        String selection = sVehiclesWithHistoryDateSelection;
        String groupBy = VehicleEntry.COLUMN_VEHICLE_REGISTRATION;
        String[] selectionArgs;

        if (type != 0){
            selectionArgs = new String[]{String.valueOf(calDate), String.valueOf(nextDate), String.valueOf(1)};
            Log.d(LOG_TAG, "Type == 1: " + selection +" : "+ type + "\n"+ Arrays.toString(projection));
        }else {
            selectionArgs = new String[]{String.valueOf(calDate), String.valueOf(nextDate), String.valueOf(0)};
            Log.d(LOG_TAG, "Type == 0: " + selection +" : "+ type + "\n"+ Arrays.toString(projection));
        }

        Log.d(LOG_TAG, "ALL_VEHICLES_WITH_TRANSACTION_AND_DATE_CODE: " + selection +" : "+ Arrays.toString(selectionArgs));
        return sVehicleWithTransactionQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                groupBy,
                null,
                sortOrder
        );
    }
    //Retrieve all Transaction items from the database
    private Cursor getTransactions(String[] projection) {

        return sTransactionByVehicleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getTransactionsByTransactionId(Uri uri, String[] projection, String sortOrder) {
        String transactionId = TransactionEntry.getTransactionIdFromUri(uri);

        String[] selectionArgs = new String[]{transactionId};
        String selection = sTransactionWithTransactionId;

        return sTransactionByVehicleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTransactionByTransactionKey(Uri uri, String[] projection, String sortOrder) {
        // ToDo : retrieve vehicleId
        String transactionKey = TransactionEntry.getVehicleRegFromUri(uri);
        String startDate = TransactionEntry.getDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        // If no date given search without considering date of transaction
        if (startDate == null) {
            selection = sTransactionWithVehicleId;
            selectionArgs = new String[]{transactionKey};

            Log.w(LOG_TAG, "called; startDate == null where; " + transactionKey +": "+ Arrays.toString(projection));
        } else {
            selection = sVehicleRegistrationWithStartDateSelection;
            selectionArgs = new String[]{transactionKey, startDate};
            Log.w(LOG_TAG, "called; startDate == "+startDate+" where; " + transactionKey +": "+ Arrays.toString(projection));
        }

        if (!transactionKey.equals("Office")){
            return sTransactionByVehicleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

        }else {

            return mOpenHelper.getReadableDatabase().query(
                    TransactionEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder

            );
        }
    }

//    private Cursor getTransactionByVehicleAndDate(Uri uri, String[] projection, String sortOrder) {
//        // ToDo : retrieve vehicleId
//        String transactionKey = TransactionEntry.getVehicleRegFromUri(uri);
//        String startDate = TransactionEntry.getStartDateFromUri(uri);
//
//        String[] selectionArgs;
//        String selection;
//
//        // If no date given search without considering date of transaction
//        if (startDate == null) {
//            selection = sTransactionWithVehicleId;
//            selectionArgs = new String[]{transactionKey};
//
//            Log.w(LOG_TAG, "called; startDate == null where; " + transactionKey +": "+ Arrays.toString(projection));
//        } else {
//            selection = sVehicleRegistrationWithStartDateSelection;
//            selectionArgs = new String[]{transactionKey, startDate};
//            Log.w(LOG_TAG, "called; startDate == "+startDate+" where; " + transactionKey +": "+ Arrays.toString(projection));
//        }
//
//        if (!transactionKey.equals("Office")){
//            return sTransactionByVehicleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null,
//                    null,
//                    sortOrder
//            );
//        }else {
//
//            return mOpenHelper.getReadableDatabase().query(
//                    TransactionEntry.TABLE_NAME,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null,
//                    null,
//                    sortOrder
//
//            );
//        }
//    }

    //Retrieve all transactions done beyond the start date given
    private Cursor getTransactionsByVehicleIdAndTransactionId(Uri uri, String[] projection, String sortOrder) {
        String vehicleId = TransactionEntry.getVehicleRegFromUri(uri);
        String transactionId = TransactionEntry.getTransactionIdFromUri(uri);

        String[] selectionArgs = new String[]{vehicleId, transactionId};
        String selection = sTransactionWithVehicleIdAndTransactionId;

        return sTransactionByVehicleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    //Retrieve all transactions done beyond the start date given
    private Cursor getTransactionsByStartDate(Uri uri, String[] projection, String sortOrder) {
//        String startDate = TransactionEntry.getStartDateFromUri(uri);

//        String[] selectionArgs = new String[]{startDate};
        String selection = sTransactionWithStartDateSelection;

        Log.w(LOG_TAG, "called; getTransactionsByStartDate:" +", "+selection);
        return sTransactionByVehicleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTransactionsByVehicleIdAndStartDate(Uri uri, String[] projection, String sortOrder) {
        String startDate = TransactionEntry.getStartDateFromUri(uri);
        String vehicleId = TransactionEntry.getVehicleRegFromUri(uri);

        String[] selectionArgs = new String[]{vehicleId, startDate};
        String selection = sTransactionsWithVehicleIdAndStartDateSelection;

        return sTransactionByVehicleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new OffloadDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Log.w(LOG_TAG, "called; query");
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case TRANSACTION_WITH_ID: {
                retCursor = getTransactionsByTransactionId(uri, projection, sortOrder);
                Log.w(LOG_TAG, "called; TRANSACTION_WITH_ID");
                break;
            }
            case TRANSACTION_WITH_VEHICLE_AND_DATE: {
//                retCursor = getTransactionsByVehicleIdAndStartDate(uri, projection, sortOrder);
                retCursor = getVehiclesAndTransactionByStartDate(uri, projection, sortOrder);

                Log.w(LOG_TAG, "called; TRANSACTION_WITH_VEHICLE_AND_DATE");
                break;
            }
            case TRANSACTION_WITH_VEHICLE_AND_START_DATE2: {
//                retCursor = getTransactionsByVehicleIdAndStartDate(uri, projection, sortOrder);
                retCursor = getTransactionByTransactionKey(uri, projection, sortOrder);

                Log.w(LOG_TAG, "called; TRANSACTION_WITH_VEHICLE_AND_DATE");
                break;
            }
            // "weather"
            case TRANSACTIONS: {

                retCursor = getTransactions(projection);
                Log.w(LOG_TAG, "called; TRANSACTIONS");
                break;
            }
            case TRANSACTIONS_WITH_VEHICLE_REGISTRATION: {
                retCursor = getTransactionByTransactionKey(uri, projection, sortOrder);
                Log.w(LOG_TAG, "called; TRANSACTIONS_WITH_VEHICLE_REGISTRATION");
                break;
            }
            case TRANSACTION_WITH_START_DATE: {

                retCursor = getTransactionsByStartDate(uri, projection, sortOrder);
                Log.w(LOG_TAG, "called; TRANSACTION_WITH_START_DATE");
                break;
            }
            // "location"
            case VEHICLE_WITH_START_DATE: {
                retCursor = getVehiclesByStartDate(uri, projection, sortOrder);

                Log.w(LOG_TAG, "called; VEHICLE_WITH_START_DATE");
                break;
            }

            case VEHICLES: {
                retCursor = getVehicles(projection, sortOrder);
                Log.w(LOG_TAG, "called; VEHICLES");
                break;
            }
            default:
                Log.w(LOG_TAG, "called; query: " + VehicleEntry.getCalDateFromUri(uri)+"/"+VehicleEntry.getNextDateFromUri(uri));
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;

    }


    @Override
    public String getType(Uri uri) {
        Log.w(LOG_TAG, "called; getType");

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case TRANSACTION_WITH_VEHICLE_AND_DATE:
                return TransactionEntry.CONTENT_ITEM_TYPE;
//            case TRANSACTION_WITH_VEHICLE_ID_AND_TRANSACTION_ID:
//                return TransactionEntry.CONTENT_TYPE;
            case TRANSACTIONS:
                return TransactionEntry.CONTENT_TYPE;
            case VEHICLES:
                return VehicleEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TRANSACTIONS: {
                long _id = db.insertWithOnConflict(TransactionEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.w(LOG_TAG, "insert; TRANSACTIONS: " + values);
                if (_id > 0)
                    uri = TransactionEntry.buildTransactionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VEHICLES: {
                long _id = db.insertWithOnConflict(VehicleEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                Log.w(LOG_TAG, "insert; VEHICLES: " + values);

                if (_id > 0)
                    uri = VehicleEntry.buildVehicleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

//    private void normalizeDate(ContentValues values) {
//        // normalize the date value
//        if (values.containsKey(VehicleContract.TransactionEntry.COLUMN_DATE_TIME)) {
//            long dateValue = values.getAsLong(VehicleContract.TransactionEntry.COLUMN_DATE_TIME);
//            values.put(VehicleContract.TransactionEntry.COLUMN_DATE_TIME, VehicleContract.normalizeDate(dateValue));
//        }
//
//    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case TRANSACTIONS:
                rowsDeleted = db.delete(
                        TransactionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VEHICLES:
                rowsDeleted = db.delete(
                        VehicleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TRANSACTIONS:
                rowsUpdated = db.update(TransactionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VEHICLES:
                rowsUpdated = db.update(VehicleEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VEHICLES:
                int returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(VehicleEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.w(LOG_TAG, "bulkInsert; VEHICLES: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case TRANSACTIONS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(TransactionEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.w(LOG_TAG, "bulkInsert; TRANSACTIONS: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


}