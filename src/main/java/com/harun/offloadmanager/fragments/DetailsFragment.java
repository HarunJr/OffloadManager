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

import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.adapters.DetailsAdapter;
import com.harun.offloadmanager.data.OffloadContract;
import com.harun.offloadmanager.helper.DividerItemDecoration;
import com.harun.offloadmanager.tasks.FetchTransactionTask;

public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String DETAIL_URI = "URI";

    Uri mUri;
    String vehicleReg;
    View rootView;
    private DetailsAdapter mDetailsAdapter;

    public TextView headerDateView;
    public TextView headerCollectionView;
    public TextView headerExpenseView;
    public TextView headerDifferenceView;

    private static final String[] TRANSACTION_COLUMNS = {
            OffloadContract.TransactionEntry.TABLE_NAME + "." + OffloadContract.TransactionEntry.COLUMN_TRANSACTION_ID,
            OffloadContract.TransactionEntry.COLUMN_AMOUNT,
            OffloadContract.TransactionEntry.COLUMN_TYPE,
            OffloadContract.TransactionEntry.COLUMN_DESCRIPTION,
            OffloadContract.TransactionEntry.COLUMN_DATE_TIME,
    };

    public static final int COL_TRANSACTION_ID = 0;
    public static final int COL_AMOUNT = 1;
    public static final int COL_TYPE = 2;
    public static final int COL_DESCRIPTION = 3;
    public static final int COL_DATE_TIME = 4;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailsFragment.DETAIL_URI);
            Log.w(LOG_TAG, "onCreateView " + mUri);
        }

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
    public void onStart() {
        super.onStart();
        updateTransactions();
    }

    private void updateTransactions() {

        String method = "vehicle_transactions";

        FetchTransactionTask transactionTask = new FetchTransactionTask(getContext());
        transactionTask.execute();

        //TODO: Use postToServerTask as an example of how to model the the tasks
//        PostToServerTask postToServerTask = new PostToServerTask(getContext());
//        postToServerTask.execute(method, vehicleReg);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        vehicleReg = OffloadContract.VehicleEntry.getVehicleRegistrationFromUri(mUri);
        Uri transactionForVehicleRegUri = OffloadContract.TransactionEntry.buildVehicleTransactionUri(vehicleReg);
        String sortOrder = OffloadContract.TransactionEntry.COLUMN_DATE_TIME + " DESC";
        Log.w(LOG_TAG, "onCreateLoader: " + vehicleReg + " " + transactionForVehicleRegUri);

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

            long dateTime = data.getLong(DetailsFragment.COL_DATE_TIME);
            double dailyTotalCollection = Double.parseDouble(OffloadContract.VehicleEntry.getDailyTotalCollectionFromUri(mUri));
            double dailyTotalExpense = Double.parseDouble(OffloadContract.VehicleEntry.getDailyTotlaExpenseFromUri(mUri));
            double difference = dailyTotalCollection - dailyTotalExpense;

            String day = DateHelper.getFormattedDayString(dateTime);
            String formattedCollection = DateHelper.getFormattedCurrency(getContext(), dailyTotalCollection);
            String formattedExpense = DateHelper.getFormattedCurrencyExpense(getContext(), dailyTotalExpense);
            String formattedDifference = DateHelper.getFormattedCurrency(getContext(), difference);

            headerDateView.setText(day);
            headerCollectionView.setText(formattedCollection);
            headerExpenseView.setText(formattedExpense);
            headerDifferenceView.setText(formattedDifference);

            mDetailsAdapter.swapCursor(data);
            Log.w(LOG_TAG, "onLoadFinished: " + data.getCount()+", "+ dailyTotalCollection);
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

//    public interface OnFabPressedListener {
//        // TODO: Update argument type and name
//        void onFabPressed(String vehicleReg);
//    }
}
