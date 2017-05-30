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
import com.harun.offloadmanager.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddUSerFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddUserFragment extends Fragment {
    public static final String LOG_TAG = AddUserFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextInputLayout phoneNumberWrapper;
    TextInputLayout nameWrapper;

    EditText etName, etPhoneNo, etEmail, etPassCode, etRole;
    Button btnAddUser;
    TextView tvLoginLink;
    AlertDialog.Builder builder;

    private OnAddUSerFragmentInteractionListener mListener;

    public AddUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddUserFragment newInstance(String param1, String param2) {
        AddUserFragment fragment = new AddUserFragment();
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
        final View rootView =  inflater.inflate(R.layout.fragment_add_user, container, false);

        nameWrapper = (TextInputLayout) rootView.findViewById(R.id.nameWrapper);
        phoneNumberWrapper = (TextInputLayout) rootView.findViewById(R.id.phoneNumberWrapper);
        phoneNumberWrapper = (TextInputLayout) rootView.findViewById(R.id.userRoleWrapper);

        etName = (EditText) rootView.findViewById(R.id.user_full_names);
        etPhoneNo = (EditText) rootView.findViewById(R.id.user_phone_register);
        etRole = (EditText) rootView.findViewById(R.id.user_company_role);

        btnAddUser = (Button) rootView.findViewById(R.id.button_add_user);

        etName.requestFocus();
        showSoftKeyboard(etName);

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view);

                Log.w(LOG_TAG, "button_register");
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNo.getText().toString();
                String role = etRole.getText().toString();

                User user = new User(name, phoneNumber, role);

                if (name.equals("") || phoneNumber.equals("") || role.equals("")) {
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
                    ((OnAddUSerFragmentInteractionListener) getActivity()).onAddUserFragmentInteraction(user);
                }
            }
        });


        return rootView;

    }

    public void hideSoftKeyboard(View view) {
        view = getActivity().getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void showSoftKeyboard(View view) {
        if (etName.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etName, 0);
        }
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onAddUserFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnAddUSerFragmentInteractionListener) {
//            mListener = (OnAddUSerFragmentInteractionListener) context;
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
    public interface OnAddUSerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAddUserFragmentInteraction(User user);
    }
}
