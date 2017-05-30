package com.harun.offloadmanager;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by HARUN on 5/13/2016.
 */
public class DateHelper {
    public static final String LOG_TAG = DateHelper.class.getSimpleName();


    public static String getFormattedDayString(long dateInMillis){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("dd/MMM/yyyy");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedDateHyphenString(long dateTimeMillis) {
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = new DateTime(dateTimeMillis, DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedMonthString(long dateInMillis){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("MMM/yyyy");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedTimeString(long dateInMillis){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern(" h:mm a");
        DateTime dateTime = new DateTime(Long.valueOf(dateInMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static String getFormattedDateFromEpoch(long dateTimeMillis) {
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:s");
        DateTime dateTime = new DateTime(dateTimeMillis * 1000, DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }
    public static String getFormattedDateTimeHyphenString(long dateTimeMillis) {
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("yyyy-MM-dd ");
        DateTime dateTime = new DateTime(Long.valueOf(dateTimeMillis), DateTimeZone.getDefault());
        return dayFormat.print(dateTime);
    }

    public static long getMilliDateTimeFromString(String stringDate){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(stringDate, dayFormat);
        return dateTime.getMillis();
    }

    public static String getFormattedCurrency(Context context, double amount){
        return context.getString(R.string.format_amount, amount);
    }
    public static String getFormattedCurrencyExpense(Context context, double amount){
        return context.getString(R.string.format_expense, amount);
    }

}
