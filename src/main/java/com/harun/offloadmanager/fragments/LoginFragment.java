package com.harun.offloadmanager.fragments;

import android.content.Context;
import android.content.DialogInterface;
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
import com.harun.offloadmanager.data.LocalStore;
import com.harun.offloadmanager.models.User;

public class LoginFragment extends Fragment {
    public static final String LOG_TAG = LoginFragment.class.getSimpleName();
    TextInputLayout passwordWrapper;
    EditText etPhoneNo, etPassword;
    Button btnLogin;
    TextView tvRegister;

    LocalStore userLocalStore;
    AlertDialog.Builder builder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        passwordWrapper = (TextInputLayout) rootView.findViewById(R.id.passwordWrapper);

        passwordWrapper.setHint("4 digit PIN");

        etPhoneNo = (EditText) rootView.findViewById(R.id.phone_no);
        etPassword = (EditText) rootView.findViewById(R.id.pin);
        btnLogin = (Button) rootView.findViewById(R.id.login_button);
        tvRegister = (TextView) rootView.findViewById(R.id.register_link);

        showSoftKeyboard(etPhoneNo);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "login_button");

                hideSoftKeyboard(etPhoneNo);

//                User user = new LocalStore(getContext()).getStoredUser();
//                String phoneNumber = etPhoneNo.getText().toString();
//                String email = etPhoneNo.getText().toString();
//                String email = "admin@transit.gemilab.com";
//                String pin = "secret";
                String email = "hr@mail.com";
                String pin = "1234";

                User user = new User(email, pin);
                checkFields(user);
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "register_link");
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_login, new RegisterFragment())
                        .commit();
            }
        });

        userLocalStore = new LocalStore(getContext());

        return rootView;
    }

    private void checkFields(User user){
        Log.w(LOG_TAG, "checkFields " + user.getEmail());
        authenticate(user);

//        if (user.getPhoneNo().equals("") || user.pin.equals("")) {
//
//            showErrorMessage();
//        } else {
//
//            authenticate(user);
//        }
    }

    private void showErrorMessage() {
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
    }

    private void authenticate(User user) {
        String registerMethod = "login_user";
        Log.w(LOG_TAG, "register_link " + user.getEmail());
        ((OnLoginFragmentInteractionListener) getActivity()).onLoginFragmentInteraction(user);

//        new ServerRequest(getActivity()).execute(registerMethod, user.phoneNo, user.pin);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput( view, 0);
        }
    }


    public void hideSoftKeyboard(View view) {
        view = getActivity().getCurrentFocus();
        Log.w(LOG_TAG, "hideSoftKeyboard "+view);

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
        }
    }

    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument company and name
        void onLoginFragmentInteraction(User uri);
    }
}
