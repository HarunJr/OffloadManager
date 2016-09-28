package com.harun.offloadmanager.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.harun.offloadmanager.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by HARUN on 8/24/2016.
 */
public class CalendarAdapter extends ArrayAdapter<Date> {
    public static final String LOG_TAG = CalendarAdapter.class.getSimpleName();
    // days with events
    private HashSet<Date> eventDays;

    // for view inflation
    private LayoutInflater inflater;

    public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
    {
        super(context, R.layout.control_calendar_day, days);
        this.eventDays = eventDays;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        // day in question
        Date date = getItem(position);
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();

        Log.w(LOG_TAG, "getView " + date +"/"+day+"/"+month+"/"+ year);

        // today
        Date today = new Date();

        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.control_calendar_day, parent, false);

        // if this day has an event, specify event image
        view.setBackgroundResource(0);
        if (eventDays != null)
        {
            for (Date eventDate : eventDays)
            {
                if (eventDate.getDate() == day &&
                        eventDate.getMonth() == month &&
                        eventDate.getYear() == year)
                {
                    // mark this day for event
                    //TODO: Add circle with accent color
//                    view.setBackgroundResource(R.drawable.ic_alarm_white_24dp);
                    break;
                }
            }
        }

        // clear styling
//        ((TextView)view).setTypeface(null, Typeface.NORMAL);
        ((TextView)view).setTextColor(Color.WHITE);

        if (month != today.getMonth() || year != today.getYear())
        {
            // if this day is outside current month, grey it out
            ((TextView)view).setTextColor(getContext().getResources().getColor(R.color.colorPrimaryText));
        }
        else if (day == today.getDate())
        {
            // if it is today, set it to blue/bold
            ((TextView)view).setTypeface(null, Typeface.BOLD);
            ((TextView)view).setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        }

        // set text
        ((TextView)view).setText(String.valueOf(date.getDate()));

        return view;
    }
}
