package com.harun.offloadmanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.models.Vehicle;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddVehicleFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddVehicleFragment extends Fragment {
    public static final String LOG_TAG = AddVehicleFragment.class.getSimpleName();
    public static String AUTH_TOKEN = null;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    EditText mRegInput;
    EditText makeInput;
    EditText modelInput;
    EditText YOMInput;
    EditText passCapInput;

    Button mAddVehicleButton;
    LocalStore localStore;


    private OnAddVehicleFragmentInteractionListener mListener;

    public AddVehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddVehicleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddVehicleFragment newInstance(String param1, String param2) {
        AddVehicleFragment fragment = new AddVehicleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        localStore = new LocalStore(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_vehicle, container, false);
        declareViews(rootView);
        return rootView;
    }

    private void declareViews(View rootView){
        mRegInput = (EditText) rootView.findViewById(R.id.vehicle_input);
        makeInput = (EditText) rootView.findViewById(R.id.vehicle_make_input);
        modelInput = (EditText) rootView.findViewById(R.id.vehicle_model_input);
        YOMInput = (EditText) rootView.findViewById(R.id.vehicle_man_year_input);
        passCapInput = (EditText) rootView.findViewById(R.id.passenger_cap_input);

        mAddVehicleButton = (Button) rootView.findViewById(R.id.create_vehicle_button);
        mAddVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "onClick ");
                    String vehicleRegistration = mRegInput.getText().toString();
                    String vehicleMake = makeInput.getText().toString();
                    String vehicleModel = modelInput.getText().toString();
                    String yearOfManufacture = YOMInput.getText().toString();
                    String passengerCapacity = passCapInput.getText().toString();
                    String dateTime = String.valueOf(System.currentTimeMillis());

                    //user data
                    User user = localStore.getStoredUser();
                    String user_key = user.getCompany();

                    Vehicle vehicle = new Vehicle(
                            user_key, vehicleRegistration,vehicleMake, vehicleModel, passengerCapacity, yearOfManufacture, dateTime );

                    Log.w(LOG_TAG, "create button clicked "+vehicleRegistration +": "+user_key);

                    AUTH_TOKEN = localStore.getToken();

                ((OnAddVehicleFragmentInteractionListener) getActivity()).onAddVehicleFragmentInteraction(vehicle);

//                    startActivity(new Intent(AddVehicleActivity.this, MainActivity.class));
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onAddVehicleFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnAddVehicleFragmentInteractionListener) {
//            mListener = (OnAddVehicleFragmentInteractionListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAddVehicleFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAddVehicleFragmentInteraction(Vehicle uri);
    }
}
