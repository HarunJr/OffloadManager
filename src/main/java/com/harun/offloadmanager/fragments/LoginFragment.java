package com.harun.offloadmanager.fragments;

import android.content.DialogInterface;
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
import com.harun.offloadmanager.tasks.ServerRequest;

public class LoginFragment extends Fragment {
    public static final String LOG_TAG = LoginFragment.class.getSimpleName();
    EditText etPhoneNo, etPassword;
    Button btnLogin;
    TextView tvRegister;

    UserLocalStore userLocalStore;
    AlertDialog.Builder builder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

        etPhoneNo = (EditText) rootView.findViewById(R.id.phone_no);
        etPassword = (EditText) rootView.findViewById(R.id.pin);
        btnLogin = (Button) rootView.findViewById(R.id.login_button);
        tvRegister = (TextView) rootView.findViewById(R.id.register_link);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w(LOG_TAG, "login_button");
                String phoneNumber = etPhoneNo.getText().toString();
                String pass_code = etPassword.getText().toString();

                User user = new User(phoneNumber, pass_code);

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

        userLocalStore = new UserLocalStore(getContext());

        return rootView;
    }

    private void checkFields(User user){
        if (user.phoneNo.equals("") || user.pin.equals("")) {
            showErrorMessage();

        } else {

            authenticate(user);
        }
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
        new ServerRequest(getActivity()).execute(registerMethod, user.phoneNo, user.pin);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
