package com.harun.offloadmanager.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HARUN on 6/25/2015.
 */
public class OffloadContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.harun.offloadmanager";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_VEHICLE = "vehicle";
    public static final String PATH_TRANSACTIONS = "transactions";

    // Format used for storing dates in the database. ALso used for converting
    // those strings back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "transactions";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);

    }

    /**
     * Converts a dateText to a long Unix time representation
     *
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static final class VehicleEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VEHICLE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE;

        public static final String TABLE_NAME = "vehicle";

        public static final String COLUMN_VEHICLE_REGISTRATION = "vehicleRegistration";
        public static final String COLUMN_VEHICLE_REGISTRATION_DATE = "signUpDate";
        public static final String COLUMN_VEHICLE_TOTAL_COLLECTION = "vehicleCollection";
        public static final String COLUMN_VEHICLE_TOTAL_EXPENSE = "vehicleExpense";
        public static final String COLUMN_LAST_TRANSACTION_DATE_TIME = "last_transaction_date_time";

        public static Uri buildVehicleUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVehicleRegistration(String vehicleRegistration)
        {
            return CONTENT_URI.buildUpon().appendPath(vehicleRegistration).build();
        }
        public static Uri buildVehicleRegistrationWithDate(String vehicleRegistration, long date)
        {
            return CONTENT_URI.buildUpon().appendPath(vehicleRegistration).appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static String getVehicleRegistrationFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }


        public static String getDateFromUri(Uri uri)
        {
            return uri.getPathSegments().get(2);
        }
    }

    public static final class TransactionEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;

        public static final String TABLE_NAME = "transactions";

        public static final String COLUMN_TRANSACTION_ID = "_id";
        public static final String COLUMN_VEHICLE_KEY = "vehicle_id";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE_TIME = "transaction_date_time";


        public static Uri buildTransactionUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTransactionsWithVehicleIdUri(long vehicleId)
        {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf((vehicleId))).build();
        }

        /*
            Create URI with vehicle Reg appeded at end of URi
         */
        public static Uri buildVehicleTransactionUri(int vehicleId)
        {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(vehicleId)).build();
        }

        public static Uri buildVehicleTransactionWithStartDate(String vehicleRegistration, long startDate)
        {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(vehicleRegistration)
                    .appendQueryParameter(COLUMN_DATE_TIME, Long.toString(normalizedDate)).build();
        }

        public static Uri buildVehicleTransactionWithDate(String vehicleRegistration, long date)
        {
            return CONTENT_URI.buildUpon().appendPath(vehicleRegistration)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }
        // Get the Transaction id from Uri
        public static String getTransactionIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static String getVehicleIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri)
        {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri)
        {
            return uri.getQueryParameter(COLUMN_DATE_TIME);
        }


    }


}
