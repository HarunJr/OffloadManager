package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.harun.offloadmanager.DateHelper;
import com.harun.offloadmanager.R;
import com.harun.offloadmanager.helper.NumPad;

/**
 * Simple Fragment containing the NumberPad and the EditText
 */
public class IncomeFragment extends Fragment {
    private static final String LOG_TAG = IncomeFragment.class.getSimpleName();

    private static final String TYPE = "type";
    private static final String VEHICLE_REG = "vehicleReg";

    private static int mType;
    private static String mVehicleReg;

    private static CoordinatorLayout mCoordinatorLayout;
    private static KeyboardView mKeyboardView;
    private static NumPad mNumPad;
    protected EditText mCollectionInput;

    private static Context mContext;

    public IncomeFragment() {
        // Required empty public constructor
    }

    public static IncomeFragment newInstance(Context context, int position, String vehicleReg, CoordinatorLayout coordinatorLayout, KeyboardView keyboardView, NumPad numPad) {
        mCoordinatorLayout = coordinatorLayout;
        mKeyboardView = keyboardView;
        mNumPad = numPad;

        mContext =context;
        mVehicleReg = vehicleReg;
        mType = position;

        IncomeFragment fragment = new IncomeFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, position);
        args.putString(VEHICLE_REG, vehicleReg);
        fragment.setArguments(args);

        Log.w(LOG_TAG, "IncomeFragment: " + mVehicleReg + ", " + mType);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE);
            mVehicleReg = getArguments().getString(VEHICLE_REG);
            Log.w(LOG_TAG, "onCreate " + mType + ", " + mVehicleReg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_income, container, false);
        mCollectionInput = (EditText) rootView.findViewById(R.id.income_input);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mTabs.setTabTextColors(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.colorAccent)));

        //Call numberPad Keyboard class
//        numPad = new NumPad(getActivity(), keyboardView,R.xml.num_pad);
        mNumPad.registerEditText(mCollectionInput, mType);
        mCollectionInput.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        mCollectionInput.requestFocus();
        Log.w(LOG_TAG, "onCreateView: " + mCollectionInput + ", " + mCollectionInput);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    public void sendCollectionData(EditText edittext) {
        Log.w(LOG_TAG, "sendCollectionData: ");
        mCollectionInput = edittext;
        if (mCollectionInput.getText().toString().trim().length() != 0){

            Snackbar.make(mCoordinatorLayout, "Sent!", Snackbar.LENGTH_LONG).setCallback(new Snackbar.Callback() {
                long dateTime = System.currentTimeMillis();
                String day = DateHelper.getFormattedDayString(dateTime);

                String method = "add_transaction";
                int collection = Integer.parseInt(mCollectionInput.getText().toString());
                int type = Integer.parseInt(String.valueOf(mType));
                String description = day;

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Log.w(LOG_TAG, "sendCollectionData: " + collection);
                    switch (event) {
                        case Snackbar.Callback.DISMISS_EVENT_SWIPE:
                            Log.w(LOG_TAG, "DISMISS_EVENT_SWIPE: " + mVehicleReg +", "+collection);
                            ((OnSendCollectionListener) mContext).onCollectionButtonClicked(
                                    mVehicleReg, method, collection, type, description);
                            break;
                        case Snackbar.Callback.DISMISS_EVENT_ACTION:
                            Log.w(LOG_TAG, "DISMISS_EVENT_ACTION: " + mContext);
                            Toast.makeText(mContext, "Clicked the action", Toast.LENGTH_LONG).show();
                            break;
                        case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                            //Post to server after 4000ms
                            Log.w(LOG_TAG, "DISMISS_EVENT_SWIPE: " + mContext);
                            Toast.makeText(mContext, "TIME OUT! Sending " + collection + " to server", Toast.LENGTH_LONG).show();

                            ((OnSendCollectionListener) mContext).onCollectionButtonClicked(
                                    mVehicleReg, method, collection, type, description);
                            break;
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {
                }
            }).setAction("Undo! ", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }

            }).setDuration(5000)
                    .show();

        }else {
            Toast.makeText(mContext, "Please Enter Collection To Continue ", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnSendCollectionListener {
        // TODO: Update argument type and name
        void onCollectionButtonClicked(String vehicleReg, String method, int collection, int type, String description);
    }
}



