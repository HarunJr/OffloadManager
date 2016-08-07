package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.VehiclesAdapter;
import com.harun.offloadmanager.activities.AddVehicleActivity;
import com.harun.offloadmanager.data.OffloadContract;
import com.harun.offloadmanager.sync.OffloadSyncAdapter;

public class VehiclesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = VehiclesFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private VehiclesAdapter mVehiclesAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    private static final int VEHICLE_LOADER = 0;

    private static final String[] VEHICLE_COLUMN = {
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
    public static final int COL_VEHICLE_AMOUNT = 2;
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vehicles, container, false);
        View emptyView = rootView.findViewById(R.id.recyclerview_vehicle_empty);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvVehicles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mVehiclesAdapter = new VehiclesAdapter(getActivity(), new VehiclesAdapter.VehiclesAdapterOnClickHandler() {
            @Override
            public void onClick( Uri uri, VehiclesAdapter.ViewHolder vh) {
                ((OnFragmentInteractionListener) getActivity()).onItemSelected(uri);

                Log.w(LOG_TAG, "onCreateView " + uri);
                mPosition = vh.getAdapterPosition();

            }
        }, emptyView);

        mRecyclerView.setAdapter(mVehiclesAdapter);

//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
//        mRecyclerView.addItemDecoration(itemDecoration);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(VEHICLE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_add_vehicle:
                Log.w(LOG_TAG, "action_add_vehicle");
                addVehicle();
                return true;
            case R.id.action_refresh:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNetworkAvailable()){
            updateView();
        }else {
            Toast.makeText(getActivity(), R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void addVehicle(){
        startActivity(new Intent(getActivity(), AddVehicleActivity.class));
    }

    private void updateView(){
//        FetchVehicleTask vehicleTask = new FetchVehicleTask(getContext());
//        vehicleTask.execute();
        OffloadSyncAdapter.syncImmediately(getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.w(LOG_TAG, "onCreateLoader: ");
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

        mVehiclesAdapter.swapCursor(data);

        if (mPosition != RecyclerView.NO_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.smoothScrollToPosition(mPosition);
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
//        void onItemSelected(Uri uri);

        void onItemSelected( Uri uri);
    }
}