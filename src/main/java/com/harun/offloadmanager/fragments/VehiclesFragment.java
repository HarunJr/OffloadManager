package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.harun.offloadmanager.Constants;
import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.activities.AddVehicleActivity;
import com.harun.offloadmanager.activities.LoginActivity;
import com.harun.offloadmanager.adapters.VehiclesAdapter;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.data.OffloadContract;
import com.harun.offloadmanager.models.Transaction;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.tasks.ServerRequest;

public class VehiclesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = VehiclesFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private VehiclesAdapter mVehiclesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private RecyclerView mRecyclerView;

    private static final String SELECTED_KEY = "selected_position";
    private static final int VEHICLE_LOADER = 0;

    public TextView headerVehicleDateView;
    public TextView headerVehicleCollectionView;
    public TextView headerVehicleExpenseView;
    public TextView headerVehicleDifferenceView;
    public CardView headerCardView;

    String listenerTag;
    double vehicleBalance;
    private long dateTime;
    public static final String[] VEHICLE_COLUMN = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the vehicle & transactions tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            OffloadContract.VehicleEntry.TABLE_NAME + "." + OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION,
            OffloadContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION_DATE,
            OffloadContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION,
            OffloadContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE,
            OffloadContract.VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME,
    };

    // These indices are tied to VEHICLE_COLUMN.  If VEHICLE_COLUMN changes, these
    // must change.
    public static final int COL_VEHICLE_REGISTRATION = 0;
    public static final int COL_REG_DATE_TIME = 1;
    public static final int COL_VEHICLE_COLLECTION = 2;
    public static final int COL_VEHICLE_EXPENSE = 3;
    public static final int COL_LAST_TRANSACTION_DATE_TIME = 4;


    public VehiclesFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VehiclesFragment newInstance(Bundle args) {
        VehiclesFragment fragment = new VehiclesFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            dateTime = getArguments().getLong(Constants.CURRENT_DAY);
            Log.w(LOG_TAG, "onCreate " + dateTime);
        } else {
            Log.w(LOG_TAG, "Error...NO ARGS " + dateTime);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vehicles, container, false);

        headerCardView = (CardView) rootView.findViewById(R.id.header_vehicles_cardView);
        headerVehicleDateView = (TextView) rootView.findViewById(R.id.item_vehicles_header_date_text_view);
        headerVehicleCollectionView = (TextView) rootView.findViewById(R.id.item_vehicles_header_collection_text_view);
        headerVehicleExpenseView = (TextView) rootView.findViewById(R.id.item_vehicles_header_expense_text_view);
        headerVehicleDifferenceView = (TextView) rootView.findViewById(R.id.item_vehicles_header_profit_text_view);

        headerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String officeKey = "Office";
                int dailyOfficeCollection = (int) vehicleBalance;

                Uri summaryUri = OffloadContract.VehicleEntry.buildVehicleRegistrationWithTransactionsAndDate(officeKey, dailyOfficeCollection, 0);//vehicleBalance, office expenses.
                ((OnFragmentInteractionListener) getActivity()).onFragmentInteraction(summaryUri, dateTime);
                Log.w(LOG_TAG, "onCreateView " + summaryUri);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        View emptyView = rootView.findViewById(R.id.recyclerview_vehicle_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvVehicles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mVehiclesAdapter = new VehiclesAdapter(getActivity(), new VehiclesAdapter.VehiclesAdapterOnClickHandler() {
            @Override
            public void onClick(Uri uri, VehiclesAdapter.ViewHolder vh) {
                listenerTag = "listItemSelected";
                ((OnFragmentInteractionListener) getActivity()).onFragmentInteraction(uri, dateTime);

                Log.w(LOG_TAG, "onCreateView " + uri);
                mPosition = vh.getAdapterPosition();

            }
        }, emptyView);

        mRecyclerView.setAdapter(mVehiclesAdapter);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
        Log.w(LOG_TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(LOG_TAG, "onStart");
        if (isNetworkAvailable()) {
            Log.w(LOG_TAG, "isNetworkAvailable()" + isNetworkAvailable());

            LocalStore localStore = new LocalStore(getContext());
            Transaction transaction = localStore.getTransactionsNotSynced();

            if (transaction != null){
                Log.w(LOG_TAG, "onStart()" + transaction.vehicleKey);

                String method = "add_transaction";
                ServerRequest serverRequest = new ServerRequest(getContext());
                serverRequest.execute(method,
                        transaction.vehicleKey, transaction.amount, transaction.type, transaction.description, transaction.dateTime);
            }

            updateView();

        } else {
            Toast.makeText(getActivity(), R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_vehicle:
                Log.w(LOG_TAG, "action_add_vehicle");
                addVehicle();
                return true;

            case R.id.action_refresh:
                if (isNetworkAvailable()) {
                    Log.w(LOG_TAG, "isNetworkAvailable()" + isNetworkAvailable());
                    updateView();
                } else {
                    Toast.makeText(getActivity(), R.string.no_internet_message, Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_logout:
                LocalStore userLocalStore = new LocalStore(getActivity());
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);

                startActivity(new Intent(getActivity(), LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.w(LOG_TAG, "isNetworkAvailable" + networkInfo);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void addVehicle() {
        startActivity(new Intent(getActivity(), AddVehicleActivity.class));
    }

    private void updateView() {
        Log.w(LOG_TAG, "updateView");
        User user = new LocalStore(getContext()).getLoggedInUser();

        String registerMethod = "fetch_all_data";
        new ServerRequest(getActivity()).execute(registerMethod, user.phoneNo);

//        new FetchTransactionTask(getActivity()).execute();
//        OffloadSyncAdapter.syncImmediately(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Log.w(LOG_TAG, "onResume");
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        if (mPosition != mRecyclerView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, mPosition);
//        }
//
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.w(LOG_TAG, "onCreateLoader: " + id + " " + args);
        String sortOrder = OffloadContract.VehicleEntry.COLUMN_LAST_TRANSACTION_DATE_TIME + " DESC";

        return new CursorLoader(
                getActivity(),
                OffloadContract.VehicleEntry.CONTENT_URI,
                VEHICLE_COLUMN,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.w(LOG_TAG, "onLoadFinished: " + data.getCount());

        if (data != null && data.moveToFirst()) {

            dateTime = System.currentTimeMillis();
            double dailyVehicleCollection;
            double dailyVehicleExpense;

            double collectionTotal = 0.0;
            double expenseTotal = 0.0;

            //TODO: Perform in content provider e.g SUM()
            do {
                dailyVehicleCollection = data.getDouble(COL_VEHICLE_COLLECTION);
                dailyVehicleExpense = data.getDouble(COL_VEHICLE_EXPENSE);
                collectionTotal += dailyVehicleCollection;
                expenseTotal += dailyVehicleExpense;

                Log.w(LOG_TAG, "dailyTotalCollection: " + collectionTotal + "--" + expenseTotal);
            }
            while (data.moveToNext());

            Log.w(LOG_TAG, "total: " + collectionTotal);
            vehicleBalance = collectionTotal - expenseTotal;

            String day = DateHelper.getFormattedDayString(dateTime);
            String formattedCollection = DateHelper.getFormattedCurrency(getContext(), collectionTotal);
            String formattedExpense = DateHelper.getFormattedCurrencyExpense(getContext(), expenseTotal);
            String formattedDifference = DateHelper.getFormattedCurrency(getContext(), vehicleBalance);

            headerVehicleDateView.setText(day);
            headerVehicleCollectionView.setText(formattedCollection);
            headerVehicleExpenseView.setText(formattedExpense);
            headerVehicleDifferenceView.setText(formattedDifference);

            mVehiclesAdapter.swapCursor(data);

            if (mPosition != RecyclerView.NO_POSITION) {
                // If we don't need to restart the loader, and there's a desired position to restore
                // to, do so now.
                mRecyclerView.smoothScrollToPosition(mPosition);
            }
//            Log.w(LOG_TAG, "onLoadFinished: " + data.getCount()+", "+ dailyTotalCollection);
        }

        //TODO: Update EmptyView
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.w(LOG_TAG, "onLoaderReset: ");
        mVehiclesAdapter.swapCursor(null);
    }

//    TODO: Fragment to Activity to Fragment communication
//    https://developer.android.com/training/basics/fragments/communicating.html

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        //        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
        void onFragmentInteraction(Uri uri, long dateTime);
    }
}
