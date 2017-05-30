package com.harun.offloadmanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.harun.offloadmanager.infrastructure.OffloadManagerApplication;

/**
 * Created by HARUN on 5/13/2016.
 */
public class Utilities {
    public static final String LOG_TAG = Utilities.class.getSimpleName();


//    public static String getFormattedDateTimeString(DetailsCursorAdapter detailsCursorAdapter, long dateInMillis){
//
//        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd/MMMM/yyyy h:mm a");
//        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.UTC);
////        Log.w(LOG_TAG, "DateTime: " + dateTime);
//        return dateTimeFormat.print(dateTime);
//    }

//    public static String getFormattedDayString(long dateInMillis){
//        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("dd/MMMM/yyyy");
//        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.UTC);
//        return dayFormat.print(dateTime);
//    }
//
//    public static String getFormattedTimeString(long dateInMillis){
//        DateTimeFormatter dayFormat = DateTimeFormat.forPattern(" h:mm a");
//        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.UTC);
//        return dayFormat.print(dateTime);
//    }

    public static String getFormattedCurrency(Context context, double amount){
        return context.getString(R.string.format_amount, amount);
    }
    public static String getFormattedCurrencyExpense(Context context, double amount){
        return context.getString(R.string.format_expense, amount);
    }

    public static boolean isNetworkAvailable(OffloadManagerApplication application) {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.w(LOG_TAG, "isNetworkAvailable" + networkInfo);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
