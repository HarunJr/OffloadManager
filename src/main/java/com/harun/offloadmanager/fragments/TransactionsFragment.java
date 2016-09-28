package com.harun.offloadmanager.fragments;

import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.activities.TransactionsActivity;
import com.harun.offloadmanager.adapters.SmartTransactionFragmentStatePagerAdapter;
import com.harun.offloadmanager.helper.NumPad;

public class TransactionsFragment extends Fragment {
    public static final String LOG_TAG = TransactionsFragment.class.getSimpleName();
    public static String POSITION = "POSITION";

    private ViewPager mPager;
    private TabLayout mTabs;
    private String vehicleReg;
    private NumPad numPad;

    CoordinatorLayout coordinatorLayout;
    KeyboardView keyboardView;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance(Bundle args) {
        TransactionsFragment fragment = new TransactionsFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicleReg = getArguments().getString(TransactionsActivity.VEHICLE_REG);

            Log.w(LOG_TAG, "onCreate " + vehicleReg + " = " + getArguments());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transactions, container, false);
//        Constants.toolbar = (Toolbar) rootView.findViewById(R.id.transactions_tool_bar);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.transactionsCoordinatorLayout);

        mPager = (ViewPager) rootView.findViewById(R.id.transactionViewpager);

        // Give the TabLayout the ViewPager
        mTabs = (TabLayout) rootView.findViewById(R.id.tab_layout);

        keyboardView = (KeyboardView) rootView.findViewById(R.id.keyboard_view);
        numPad = new NumPad(getActivity(), keyboardView, R.xml.num_pad);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.w(LOG_TAG, "onCreateView: " + vehicleReg + ", " + getActivity());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SmartTransactionFragmentStatePagerAdapter pagerAdapter = new SmartTransactionFragmentStatePagerAdapter(
                getActivity().getSupportFragmentManager(), getActivity(), vehicleReg, coordinatorLayout, keyboardView, numPad);
        this.mPager.setAdapter(pagerAdapter);
        // Bind the slidingTabStrips to the ViewPager
//        mTabs.setTabTextColors(Color.parseColor("#ED1946"), Color.parseColor("#10EDC5"));

        this.mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                //TODO: Handle flickering of tabText when changing
                switch (position) {
                    case 0:
                        mTabs.setTabTextColors(Color.parseColor("#ED1946"), Color.parseColor("#10EDC5"));
                        mTabs.setSelectedTabIndicatorColor(Color.parseColor("#10EDC5"));
                        break;
                    case 1:
                        mTabs.setTabTextColors(Color.parseColor("#10EDC5"), Color.parseColor("#ED1946"));
                        mTabs.setSelectedTabIndicatorColor(Color.parseColor("#ED1946"));
                        break;
                }
            }
        });

        this.mTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        this.mTabs.setupWithViewPager(mPager);
    }

}
