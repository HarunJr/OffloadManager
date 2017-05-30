package com.harun.offloadmanager.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.adapters.DetailsAdapter;
import com.harun.offloadmanager.data.OffloadContract;
import com.harun.offloadmanager.helper.DividerItemDecoration;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Uri mUri;
    String transactionKey;
    View rootView;
    private DetailsAdapter mDetailsAdapter;

    public TextView headerDateView;
    public TextView headerCollectionView;
    public TextView headerExpenseView;
    public TextView headerDifferenceView;
    long dateTime;
    public static final String[] TRANSACTION_COLUMNS = {
            OffloadContract.TransactionEntry.TABLE_NAME + "." + OffloadContract.TransactionEntry.COLUMN_TRANSACTION_ID,
            OffloadContract.TransactionEntry.COLUMN_VEHICLE_KEY,
            OffloadContract.TransactionEntry.COLUMN_AMOUNT,
            OffloadContract.TransactionEntry.COLUMN_TYPE,
            OffloadContract.TransactionEntry.COLUMN_DESCRIPTION,
            OffloadContract.TransactionEntry.TIMESTAMP,
    };

    public static final int COL_TRANSACTION_ID = 0;
    public static final int COL_VEHICLE_KEY = 1;
    public static final int COL_AMOUNT = 2;
    public static final int COL_TYPE = 3;
    public static final int COL_DESCRIPTION = 4;
    public static final int COL_DATE_TIME = 5;

    public DetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(boolean twoPane) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(Constants.DETAIL_URI);

            Log.w(LOG_TAG, "onCreate " + mUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        headerDateView = (TextView) rootView.findViewById(R.id.item_header_date_text_view);
        headerCollectionView = (TextView) rootView.findViewById(R.id.item_header_collection_text_view);
        headerExpenseView = (TextView) rootView.findViewById(R.id.item_header_expense_text_view);
        headerDifferenceView = (TextView) rootView.findViewById(R.id.item_header_profit_text_view);

        View emptyView = rootView.findViewById(R.id.stickylistheaders_details_empty);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvDetails);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mDetailsAdapter = new DetailsAdapter(getActivity(), emptyView);

        mRecyclerView.setAdapter(mDetailsAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        Log.w(LOG_TAG, "onCreateView " + mDetailsAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        int TRANSACTION_LOADER = 1;
        getLoaderManager().initLoader(TRANSACTION_LOADER, null, this);
        Log.w(LOG_TAG, "onActivityCreated: " + TRANSACTION_LOADER);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int vehicleKeyFromUri = OffloadContract.VehicleEntry.getVehicleIdFromUri(mUri);
        long dayInMilli = (24 * 60 * 60) * 1000;
        long tomorrow = Constants.dateMilli + dayInMilli;
        String todayDate = OffloadContract.VehicleEntry.getDateFromUri(mUri)+ " 03:00:00";
        final String endDate = DateHelper.getFormattedDateHyphenString(tomorrow) + " 03:00:00";

        Uri transactionForVehicleRegUri = OffloadContract.TransactionEntry.buildKeyTransactionWithDateUri(vehicleKeyFromUri, todayDate, endDate);
        String sortOrder = OffloadContract.TransactionEntry.TIMESTAMP + " DESC";
        Log.w(LOG_TAG, "onCreateLoader: " + vehicleKeyFromUri + " " +dateTime+" "+ transactionForVehicleRegUri);

        if (null != mUri) {

            return new CursorLoader(
                    getActivity(),
                    transactionForVehicleRegUri,
                    TRANSACTION_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            double officeExpense = 0.00;
            double dailyTotalCollection = Double.parseDouble(OffloadContract.VehicleEntry.getDailyTotalCollectionFromUri(mUri));
            double dailyTotalExpense = Double.parseDouble(OffloadContract.VehicleEntry.getDailyTotalExpenseFromUri(mUri));

            if (transactionKey.equals("Office")){
                do {
                    dailyTotalExpense += data.getInt(DetailsFragment.COL_AMOUNT);
                }while (data.moveToNext());
            }

            //Add to get balance because 'dailyTotalExpense' is negative(-)
            double balance = dailyTotalCollection + dailyTotalExpense;

            String day = DateHelper.getFormattedDayString(dateTime);
            String formattedCollection = DateHelper.getFormattedCurrency(getContext(), dailyTotalCollection);
            String formattedExpense = DateHelper.getFormattedCurrency(getContext(), dailyTotalExpense);
            String formattedDifference = DateHelper.getFormattedCurrency(getContext(), balance);

            headerDateView.setText(day);
            headerCollectionView.setText(formattedCollection);
            headerExpenseView.setText(formattedExpense);
            headerDifferenceView.setText(formattedDifference);

            mDetailsAdapter.swapCursor(data);
            Log.w(LOG_TAG, "onLoadFinished: " + data.getCount()+", "+ dailyTotalCollection+", "+ officeExpense);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.w(LOG_TAG, "onLoaderReset: " + loader.toString());
        mDetailsAdapter.swapCursor(null);
    }

    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onVehicleFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    public interface OnFabPressedListener {
//        // TODO: Update argument company and name
//        void onFabPressed(String transactionKey);
//    }
}
