package com.harun.offloadmanager.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

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
        public static Uri buildVehicleRegistrationWithTransactionsAndDate(String vehicleRegistration, int dailyTotalCollection, int dailyTotlaExpense)
        {
            return CONTENT_URI.buildUpon()
                    .appendPath(vehicleRegistration)
                    .appendPath(Long.toString(dailyTotalCollection))
                    .appendPath(Long.toString(dailyTotlaExpense )).build();
        }

        public static String getVehicleRegistrationFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }
        public static String getDailyTotalCollectionFromUri(Uri uri)
        {
            return uri.getPathSegments().get(2);
        }
        public static String getDailyTotalExpenseFromUri(Uri uri)
        {
            return uri.getPathSegments().get(3);
        }

        public static Uri buildVehiclesWithTransactionsAndDate(long minDate, long maxDate)
        {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(minDate))
                    .appendPath(Long.toString(maxDate))
                    .build();
        }

        public static long getCalDateFromUri(Uri uri)
        {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getNextDateFromUri(Uri uri)
        {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    public static final class TransactionEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;

        public static final String TABLE_NAME = "transactions";

        public static final String COLUMN_TRANSACTION_ID = "_id";
        public static final String COLUMN_VEHICLE_KEY = "vehicle_Key";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE_TIME = "transaction_date_time";

        public static Uri buildTransactionUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVehicleWithDateAndType(long minDate, long maxDate, int type)
        {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(minDate))
                    .appendPath(Long.toString(maxDate))
                    .appendPath(Long.toString(type))
                    .build();
        }

        public static long getCalDateFromTransactionsUri(Uri uri)
        {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getNextDateFromTransactionsUri(Uri uri)
        {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static int getTypeFromUri(Uri uri)
        {
            return Integer.parseInt(uri.getPathSegments().get(3));
        }



        public static Uri buildTransactionsWithVehicleIdUri(String vehicleReg)
        {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf((vehicleReg))).build();
        }

        /*
            Create URI with vehicle Reg appeded at end of URi
         */
        public static Uri buildKeyTransactionWithDateUri(String vehicleReg, long dateTime)
        {
            return CONTENT_URI.buildUpon()
                    .appendPath(vehicleReg)
                    .appendPath(Long.toString(dateTime))
                    .build();
        }

        // Get the Transaction id from Uri
        public static String getTransactionIdFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri)
        {
            return uri.getPathSegments().get(2);
        }
        public static String getVehicleRegFromUri(Uri uri)
        {
            return uri.getPathSegments().get(1);
        }


        public static String getStartDateFromUri(Uri uri)
        {
            return uri.getQueryParameter(COLUMN_DATE_TIME);
        }

    }
}
