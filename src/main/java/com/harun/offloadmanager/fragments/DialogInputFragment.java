package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harun.offloadmanager.R;

/**
 * Created by HARUN on 5/14/2016.
 */
public class DialogInputFragment extends DialogFragment {
    private static final String LOG_TAG = DialogInputFragment.class.getSimpleName();

    private TextView mDescriptionView;
    private EditText mDescriptionInput;
    private Button mPositiveButton;

    String vehicleReg;
    int type;
    int expense;

    private static Context mContext;

    private static final String VEHICLE_REG = "transactionKey";
    private static final String EXPENSE = "expense";
    private static final String TYPE = "type";

    public DialogInputFragment(){
        //Empty constructor
    }

    public static DialogInputFragment newInstance( String vehicleReg, int expense, int type) {
        DialogInputFragment fragment = new DialogInputFragment();
        Bundle args = new Bundle();

        args.putString(VEHICLE_REG, vehicleReg);
        args.putInt(EXPENSE, expense);
        args.putInt(TYPE, type);

        fragment.setArguments(args);
        Log.w(LOG_TAG, "AddExpense: "+expense);
        return fragment;    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_input, container);
        mDescriptionView = (TextView) rootView.findViewById(R.id.dialogTextView);
        mDescriptionInput = (EditText) rootView.findViewById(R.id.input_description);
        mPositiveButton = (Button) rootView.findViewById(R.id.yes);

        vehicleReg = getArguments().getString(VEHICLE_REG);
        expense = getArguments().getInt(EXPENSE);
        type = getArguments().getInt(TYPE);

        mDescriptionView.setText(" What is the "+expense+" For?");
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = mDescriptionInput.getText().toString();
                sendBackToActivity(vehicleReg, type, expense, description);
                Log.w(LOG_TAG, "description " + expense + ": " + description);
                dismiss();
            }
        });
        return rootView;
    }

    public void sendBackToActivity(String vehicleReg, int type, int expense, String description){

        ((OnSendDescriptionListener) getActivity()).onPositiveButtonClicked(vehicleReg, type, expense, description);
        Log.w(LOG_TAG, "sendBackToActivity " + expense + ": " + description);
        dismiss();

    }

    public interface OnSendDescriptionListener {
        void onPositiveButtonClicked(String vehicleReg, int type, int expense, String description);
    }


}
