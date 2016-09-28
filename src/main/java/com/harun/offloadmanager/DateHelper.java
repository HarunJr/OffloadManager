package com.harun.offloadmanager;

import android.content.Context;

import com.harun.offloadmanager.adapters.DetailsAdapter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by HARUN on 5/13/2016.
 */
public class DateHelper {
    public static final String LOG_TAG = DateHelper.class.getSimpleName();

    public static String getFormattedDateTimeString(DetailsAdapter detailsAdapter, long dateInMillis){

        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd/MMMM/yyyy h:mm a");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
//        Log.w(LOG_TAG, "DateTime: " + dateTime);
        return dateTimeFormat.print(dateTime);
    }

    public static String getFormattedSimpleDayFromString(String calendarString){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("d/M/yyyy");
        DateTime dateTime = new DateTime(calendarString, DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }
    public static String getFormattedSimpleDayString(long dateInMillis){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("d/M/yyyy");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedDayString(long dateInMillis){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("dd/MMMM/yyyy");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedTimeString(long dateInMillis){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern(" h:mm a");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedCurrency(Context context, double amount){
        return context.getString(R.string.format_amount, amount);
    }
    public static String getFormattedCurrencyExpense(Context context, double amount){
        return context.getString(R.string.format_expense, amount);
    }
}
