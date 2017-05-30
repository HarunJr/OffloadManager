package com.harun.offloadmanager.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.Utilities;
import com.harun.offloadmanager.fragments.VehiclesFragment;
import com.harun.offloadmanager.sync.OffloadSyncAdapter;

/**
 * Created by HARUN on 9/14/2016.
 */
public class VehiclesHistoryAdapter extends RecyclerView.Adapter<VehiclesHistoryAdapter.ViewHolder> {
    public static final String LOG_TAG = VehiclesHistoryAdapter.class.getSimpleName();
    private Cursor mCursor;
    final private VehiclesHistoryAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private Context mContext;

    private String vehicleReg;
    private double expense;
    private double amount;

    private String bvehicleReg;
    private double bexpense;
    private double bamount;

    public VehiclesHistoryAdapter(Context context, VehiclesHistoryAdapterOnClickHandler mClickHandler, double collection, double expense, View emptyView) {
        this.mClickHandler = mClickHandler;
        mEmptyView = emptyView;
        mContext = context;
        Log.w(LOG_TAG, "VehiclesHistoryAdapter: " + vehicleReg +"--"+collection+"--"+expense);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false);
        view.setFocusable(true);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Log.w(LOG_TAG, "onBindViewHolder: " + mCursor.getCount() + ", " + position + " vehicle:"
                + mCursor.getString(VehiclesFragment.COL_VEHICLE_REGISTRATION)
                + "--" + mCursor.getString(mCursor.getColumnIndex("COLLECTION"))+"--"+mCursor.getString(mCursor.getColumnIndex("EXPENSE")));

        // Read values amount from cursor
//        String dateTimeString = mCursor.getString(VehiclesFragment.COL_LAST_TRANSACTION_DATE_TIME);
//        int company = mCursor.getInt(DetailsFragment.COL_TYPE);
        vehicleReg = mCursor.getString(VehiclesFragment.COL_VEHICLE_REGISTRATION);

        String formattedAmount = null;
        String formattedExpense = null;

        OffloadSyncAdapter offloadSyncAdapter = new OffloadSyncAdapter(mContext, true);

 //       Log.w(LOG_TAG, "vehicleHistory: nonDupList:" + vehicleModel.getVehicleRegistration());

        expense = mCursor.getDouble(mCursor.getColumnIndex("EXPENSE"));
        amount = mCursor.getDouble(mCursor.getColumnIndex("COLLECTION"));

        formattedExpense = Utilities.getFormattedCurrencyExpense(mContext, expense);
        formattedAmount = Utilities.getFormattedCurrency(mContext, amount);

//        Log.w(LOG_TAG, "onBindViewHolder: "+vehicleReg+", "+amount+" "+expense);

//        String dayTime = DateHelper.getFormattedTimeString(dateTimeMillis);
//        String day = DateHelper.getFormattedDayString(dateTimeMillis);

        // Find TextView and set formatted date on it
        holder.vehicleView.setText(vehicleReg);
        holder.amountView.setText(formattedAmount);
        holder.expenseView.setText(formattedExpense);
//        holder.dateView.setText(dayTime);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        Log.w(LOG_TAG, "swapCursor: " + mCursor.getCount());
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView vehicleView;
        final TextView amountView;
        final TextView dateView;
        final TextView expenseView;

        ViewHolder(View itemView) {
            super(itemView);

            vehicleView = (TextView) itemView.findViewById(R.id.item_vehicle_reg);
            amountView = (TextView) itemView.findViewById(R.id.item_vehicle_amount);
            dateView = (TextView) itemView.findViewById(R.id.item_vehicle_date_time);
            expenseView = (TextView) itemView.findViewById(R.id.item_vehicle_expense);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String vehicleReg = mCursor.getString(VehiclesFragment.COL_VEHICLE_REGISTRATION);
            int dailyTotalCollection = (int) mCursor.getDouble(mCursor.getColumnIndex("COLLECTION"));
            int dailyTotalExpense = (int) mCursor.getDouble(mCursor.getColumnIndex("EXPENSE"));
//            mClickHandler.onClick(OffloadContract.VehicleEntry.buildTransactionWithVehicleId(vehicleId, vehicleReg, dailyTotalCollection, dailyTotalExpense), this);
            Log.w(LOG_TAG, "onClick: " +vehicleReg+"--"+ amount+"--"+dailyTotalExpense);

        }
    }

    public static interface VehiclesHistoryAdapterOnClickHandler {
        void onClick(Uri uri, VehiclesHistoryAdapter.ViewHolder vh);
    }


    public void swapCursor(Cursor newCursor) {
//        mCursor = newCursor;
        Cursor[] cursors = {mCursor, newCursor};
        mCursor = new MergeCursor(cursors);
        notifyDataSetChanged();

        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
//        Log.w(LOG_TAG, "swapCursor: "+mCursor.getCount());
    }

}
