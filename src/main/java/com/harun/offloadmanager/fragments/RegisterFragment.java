package com.harun.offloadmanager.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.User;
import com.harun.offloadmanager.UserLocalStore;
import com.harun.offloadmanager.activities.LoginActivity;
import com.harun.offloadmanager.tasks.ServerRequest;


public class RegisterFragment extends Fragment {
    public static final String LOG_TAG = RegisterFragment.class.getSimpleName();
    EditText etName, etPhoneNo, etEmail, etPassCode;
    Button btnSignUp;
    TextView tvLoginLink;

    UserLocalStore localStore;

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

        etName = (EditText) rootView.findViewById(R.id.name_register);
        etPhoneNo = (EditText) rootView.findViewById(R.id.phone_register);
        etEmail = (EditText) rootView.findViewById(R.id.email_register);
        etPassCode = (EditText) rootView.findViewById(R.id.pass_code_register);

        btnSignUp = (Button) rootView.findViewById(R.id.button_register);
        tvLoginLink = (TextView) rootView.findViewById(R.id.login_link);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.w(LOG_TAG, "button_register");
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNo.getText().toString();
                String email = etEmail.getText().toString();
                String passCode = etPassCode.getText().toString();

                User user = new User(name, phoneNumber, email, passCode);

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
                    registerUser(user);
                    //                startActivity(new Intent(this, MainActivity.class));

                }
            }
        });

        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "login_link");
                startActivity(new Intent(getActivity(), LoginActivity.class));

            }
        });
        return rootView;
    }

    private void registerUser(User user) {
        String registerMethod = "register_user";
        new ServerRequest(getActivity()).execute(registerMethod, user.name, user.phoneNo, user.email, user.pin);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
