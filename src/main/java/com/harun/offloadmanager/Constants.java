package com.harun.offloadmanager;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by HARUN on 4/3/2016.
 */
public class Constants {
    public static Toolbar toolbar = null;
    public static TextView textView = null;
    public static final String VEHICLE_ID = "_id";
    public static final String VEHICLE_REG = "vehicleReg";
    public static final String DAILY_TOTAL_COLLECTION = "vehicleCollection";
    public static final String DAILY_TOTAL_EXPENSE = "vehicleExpense";
    public static final String MIN_DATE = "minDate";

    public static final String CALENDAR_DAY ="calendarDay" ;
    public static final String NEXT_DAY ="nextDay" ;
    /**
     The caller references the constants using <tt>Consts.EMPTY_STRING</tt>,
     and so on. Thus, the caller should be prevented from constructing objects of
     this class, by declaring this private constructor.
     */
    protected Constants(){
        //this prevents even the native class from
        //calling this ctor as well :
        throw new AssertionError();
    }
}
