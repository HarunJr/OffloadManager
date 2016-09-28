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
import com.harun.offloadmanager.adapters.VehiclesHistoryAdapter;
import com.harun.offloadmanager.data.OffloadContract;


public class VehiclesHistory extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = VehiclesHistory.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private static final int COLLECTION_LOADER = 0;
    private static final int EXPENSE_LOADER = 1;

    private static final String SELECTED_KEY = "selected_position";

    private VehiclesHistoryAdapter mVehiclesHistoryAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private long calMilliTime;
    private long dayNext;

    public TextView headerVehicleDateView;
    public TextView headerVehicleCollectionView;
    public TextView headerVehicleExpenseView;
    public TextView headerVehicleDifferenceView;

    View emptyView;

    public VehiclesHistory() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VehiclesHistory newInstance() {
        VehiclesHistory fragment = new VehiclesHistory();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            calMilliTime = getArguments().getLong(Constants.CALENDAR_DAY);
            dayNext = getArguments().getLong(Constants.NEXT_DAY);
            Log.w(LOG_TAG, "onCreate " + calMilliTime + ": " + dayNext);
        } else {
            Log.w(LOG_TAG, "onCreate " + calMilliTime);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vehicles_history, container, false);

        headerVehicleDateView = (TextView) rootView.findViewById(R.id.item_history_vehicles_header_date_text_view);
        headerVehicleCollectionView = (TextView) rootView.findViewById(R.id.item_history_vehicles_header_collection_text_view);
        headerVehicleExpenseView = (TextView) rootView.findViewById(R.id.item_history_vehicles_header_expense_text_view);
        headerVehicleDifferenceView = (TextView) rootView.findViewById(R.id.item_history_vehicles_header_profit_text_view);


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        emptyView = rootView.findViewById(R.id.recyclerview_vehicle_history_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_history_vehicles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mVehiclesHistoryAdapter = new VehiclesHistoryAdapter(getActivity(), emptyView);
        mRecyclerView.setAdapter(mVehiclesHistoryAdapter);


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        int[] loaderArray = {COLLECTION_LOADER, EXPENSE_LOADER};

        for (int aLoaderArray : loaderArray) {
            getLoaderManager().initLoader(aLoaderArray, null, this);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.w(LOG_TAG, "onCreateLoader: " + id);

//        Uri transactionForVehicleDateUri = OffloadContract.VehicleEntry.buildVehiclesWithTransactionsAndDate(calMilliTime, dayNext);
//        String sortOrder = OffloadContract.VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME + " DESC";

        switch (id) {
            case COLLECTION_LOADER: {
                Uri collectionForVehicleWithDateUri = OffloadContract.TransactionEntry.buildVehiclesCollectionWithDateAndType
                        (calMilliTime, dayNext, COLLECTION_LOADER);

                String[] projection = {OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION,
                        OffloadContract.TransactionEntry.COLUMN_AMOUNT + " AS EXPENSE ",
                        OffloadContract.TransactionEntry.COLUMN_AMOUNT + " AS COLLECTION ",
                                 OffloadContract.TransactionEntry.COLUMN_TYPE};

                Log.w(LOG_TAG, "COLLECTION_LOADER: >>>" + COLLECTION_LOADER + "," + collectionForVehicleWithDateUri);
                return new CursorLoader(
                        getActivity(),
                        collectionForVehicleWithDateUri,
                        projection,
                        null,
                        null,
                        null
                );
            }
            case EXPENSE_LOADER: {
                Uri expenseForVehicleWithDateUri = OffloadContract.TransactionEntry.buildVehiclesCollectionWithDateAndType
                        (calMilliTime, dayNext, EXPENSE_LOADER);

                String[] projection = {OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION,
                        OffloadContract.TransactionEntry.COLUMN_AMOUNT + " AS COLLECTION ",
                        OffloadContract.TransactionEntry.COLUMN_AMOUNT + " AS EXPENSE ",
                                 OffloadContract.TransactionEntry.COLUMN_TYPE};

                Log.w(LOG_TAG, "EXPENSE_LOADER: >>>" + EXPENSE_LOADER + "," + expenseForVehicleWithDateUri);
                return new CursorLoader(
                        getActivity(),
                        expenseForVehicleWithDateUri,
                        projection,
                        null,
                        null,
                        null
                );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        headerVehicleDateView.setText(DateHelper.getFormattedDayString(calMilliTime));

        switch (loader.getId()) {
            case COLLECTION_LOADER: {
                if (mVehiclesHistoryAdapter != null && data != null) {
                    mVehiclesHistoryAdapter.swapCursor(data);
                    mVehiclesHistoryAdapter.notifyDataSetChanged();
                    Log.w(LOG_TAG, "COLLECTION_LOADER: " + data.getCount() + ", " + loader.getId());
                }
                break;
            }
            case EXPENSE_LOADER: {
                if (mVehiclesHistoryAdapter != null && data != null) {
                    mVehiclesHistoryAdapter.swapCursor(data);
                    mVehiclesHistoryAdapter.notifyDataSetChanged();
                    Log.w(LOG_TAG, "EXPENSE_LOADER: " + data.getCount() + ", " + loader.getId());
                }
                break;
            }
        }

//        if (mVehiclesHistoryAdapter != null && data != null){
//            mVehiclesHistoryAdapter.swapCursor(data);
//            mVehiclesHistoryAdapter.notifyDataSetChanged();
//        }

        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mVehiclesHistoryAdapter.swapCursor(null);
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemSelected(Uri uri);
    }
}
