package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.harun.offloadmanager.helper.NumPad;
import com.harun.offloadmanager.R;


/**
 * Simple Fragment containing the NumberPad and the EditText
 */
public class ExpenseFragment extends Fragment {
    private static final String LOG_TAG = ExpenseFragment.class.getSimpleName();

    private static final String TYPE = "type";
    private static final String VEHICLE_REG = "vehicleReg";
    private static int mType;
    private static String mVehicleReg;

    private static CoordinatorLayout mCoordinatorLayout;
    private static KeyboardView mKeyboardView;
    private static NumPad mNumPad;
    protected EditText mExpenseInput;
    private static Context mContext;
    private static TabLayout mTabs;
    public ExpenseFragment() {
        // Required empty public constructor
    }

    public static ExpenseFragment newInstance(Context context, int position, String vehicleReg,
                                              CoordinatorLayout coordinatorLayout, KeyboardView keyboardView, NumPad numPad) {
        mCoordinatorLayout = coordinatorLayout;
        mKeyboardView = keyboardView;
        mNumPad = numPad;

        mContext = context;
        mVehicleReg = vehicleReg;
        mType = position;
        ExpenseFragment expenseFragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, position);
        args.putString(VEHICLE_REG, vehicleReg);
        expenseFragment.setArguments(args);

        Log.w(LOG_TAG, "newInstance: " + mType + ", " + mVehicleReg);
        return expenseFragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_expense, container, false);
        mExpenseInput = (EditText) rootView.findViewById(R.id.expense_input);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mTabs.setTabTextColors(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.colorAccent)));

        mExpenseInput.requestFocus();
        //Call numberPad Keyboard class
//        numPad = new NumPad(getActivity(), keyboardView,R.xml.num_pad);
        mNumPad.registerEditText(mExpenseInput, mType);
        Log.w(LOG_TAG, "onCreateView: " + mExpenseInput + ", " + mVehicleReg);
    }

    public void openDescriptionDialogue(EditText edittext) {
        Log.w(LOG_TAG, "sendExpenseData: ");
        mExpenseInput = edittext;

        String method = "transact";
        int expense = Integer.parseInt(mExpenseInput.getText().toString());
        int type = Integer.parseInt(String.valueOf(mType));

        Log.w(LOG_TAG, "onCreateView: " + expense + ", " + type);
        ((OnSendExpenseListener) mContext).onExpenseButtonClicked(
                mVehicleReg, method, expense, type);
    }

    public interface OnSendExpenseListener {
        // TODO: Update argument type and name
        void onExpenseButtonClicked(String mVehicleReg, String method, int expense, int type);
    }

}
