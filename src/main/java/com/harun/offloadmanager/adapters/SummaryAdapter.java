package com.harun.offloadmanager.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.DetailsFragment;

/**
 * Created by HARUN on 10/25/2016.
 */

public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final String LOG_TAG = DetailsAdapter.class.getSimpleName();

    private Cursor mCursor;
    final private Context mContext;
    final private View mEmptyView;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public SummaryAdapter(Context context, View emptyView) {
        this.mContext = context;
        this.mEmptyView = emptyView;

        Log.w(LOG_TAG, "DetailsAdapter: " + mContext);
    }

    private class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView dateTextView;
        TextView descriptionTextView;

        TransactionViewHolder(View itemView) {
            super(itemView);

            amountTextView = (TextView) itemView.findViewById(R.id.item_details_amount);
            dateTextView = (TextView) itemView.findViewById(R.id.item_details_date_time);
            descriptionTextView = (TextView) itemView.findViewById(R.id.item_details_description);
            Log.w(LOG_TAG, "TransactionViewHolder: ");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.w(LOG_TAG, "onCreateViewHolder: ");

        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_details, parent, false);
        rootView.setFocusable(true);
        return new SummaryAdapter.TransactionViewHolder(rootView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Log.w(LOG_TAG, "onBindViewHolder: ");

        int type = mCursor.getInt(DetailsFragment.COL_TYPE);
        double transactionAmount = mCursor.getDouble(DetailsFragment.COL_AMOUNT);
        long dateTime = mCursor.getLong(DetailsFragment.COL_DATE_TIME);
        String description = mCursor.getString(DetailsFragment.COL_DESCRIPTION);

        String dayTime = DateHelper.getFormattedTimeString(dateTime);
        String day = DateHelper.getFormattedDayString(dateTime);
        String formattedAmount;

        if (type != 0){
            formattedAmount = DateHelper.getFormattedCurrencyExpense(mContext, transactionAmount);
        }else {
            formattedAmount = DateHelper.getFormattedCurrency(mContext, transactionAmount);
        }

        ((SummaryAdapter.TransactionViewHolder) holder).amountTextView.setText(formattedAmount);
        ((SummaryAdapter.TransactionViewHolder) holder).dateTextView.setText(dayTime);
        ((SummaryAdapter.TransactionViewHolder) holder).descriptionTextView.setText(description);
        Log.w(LOG_TAG, "onBindViewHolder: " + description + ", " + transactionAmount + ", " + type);
    }

    @Override
    public int getItemCount() {
//        Log.w(LOG_TAG, "getItemCount: ");
        if (null == mCursor) return 0;
        Log.w(LOG_TAG, "getItemCount: " + mCursor.getCount());
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        Log.w(LOG_TAG, "getItemViewType: " + position);
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void swapCursor(Cursor newCursor) {
        Log.w(LOG_TAG, "getItemViewType: " + newCursor);
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }


}
