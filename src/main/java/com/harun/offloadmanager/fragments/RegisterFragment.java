package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.activities.LoginActivity;
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.models.User;

public class RegisterFragment extends Fragment {
    public static final String LOG_TAG = RegisterFragment.class.getSimpleName();
    TextInputLayout passwordWrapper;
    TextInputLayout emailWrapper;

    EditText etName, etPhoneNo, etEmail, etPassCode, etCompany;
    Button btnSignUp;
    TextView tvLoginLink;

    LocalStore localStore;

    AlertDialog.Builder builder;

    public RegisterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(LOG_TAG, "onCreate RegisterFragment");
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        emailWrapper = (TextInputLayout) rootView.findViewById(R.id.emailWrapper);
        passwordWrapper = (TextInputLayout) rootView.findViewById(R.id.passwordWrapper);

        etName = (EditText) rootView.findViewById(R.id.name_register);
        etPhoneNo = (EditText) rootView.findViewById(R.id.phone_register);
        etEmail = (EditText) rootView.findViewById(R.id.email_register);
        etCompany = (EditText) rootView.findViewById(R.id.company);
        etPassCode = (EditText) rootView.findViewById(R.id.pin_register);

        btnSignUp = (Button) rootView.findViewById(R.id.button_register);
        tvLoginLink = (TextView) rootView.findViewById(R.id.login_link);

        etName.requestFocus();
        showSoftKeyboard(etName);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftKeyboard(view);

                Log.w(LOG_TAG, "button_register");
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNo.getText().toString();
                String email = etEmail.getText().toString();
                String company = etCompany.getText().toString();
                String passCode = etPassCode.getText().toString();

                User user = new User(name, phoneNumber, email, company, passCode);

                if (email.equals("") || phoneNumber.equals("") || passCode.equals("")) {

                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Something Went Wrong !!");
                    builder.setMessage("Please fill all the fields");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    ((OnRegistrationFragmentInteractionListener) getActivity()).onRegistrationFragmentInteraction(user);
//                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "login_link");
                startActivity(new Intent(getActivity(), LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
        return rootView;
    }

    public void showSoftKeyboard(View view) {
        if (etName.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etName, 0);
        }
    }

    public void hideSoftKeyboard(View view) {
        view = getActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public interface OnRegistrationFragmentInteractionListener {
        // TODO: Update argument company and name
        void onRegistrationFragmentInteraction(User user);
    }
}
