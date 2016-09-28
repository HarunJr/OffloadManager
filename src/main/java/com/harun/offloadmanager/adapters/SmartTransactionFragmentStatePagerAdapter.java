package com.harun.offloadmanager.adapters;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.harun.offloadmanager.helper.NumPad;
import com.harun.offloadmanager.fragments.ExpenseFragment;
import com.harun.offloadmanager.fragments.IncomeFragment;

public class SmartTransactionFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = SmartTransactionFragmentStatePagerAdapter.class.getSimpleName();
    private String tabTitles[] = new String[]{"COLLECTION", "EXPENSE"};
    final int PAGE_COUNT = 2;
    private Context context;

    private KeyboardView keyboardView;
    private NumPad numPad;
    private String vehicleReg;

    CoordinatorLayout coordinatorLayout;
    public SmartTransactionFragmentStatePagerAdapter(FragmentManager fm, Context context, String vehicleReg, CoordinatorLayout coordinatorLayout, KeyboardView keyboardView, NumPad numPad) {
        super(fm);
        this.context = context;
        this.vehicleReg = vehicleReg;
        this.keyboardView = keyboardView;
        this.numPad = numPad;
        this.coordinatorLayout = coordinatorLayout;
        Log.w(LOG_TAG, "IncomeFragment: " + vehicleReg);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                // Home -Tweet fragment activity
                return IncomeFragment.newInstance(context, position, vehicleReg, coordinatorLayout, keyboardView, numPad);
            }
            case 1: {
                // Favourite fragment activity
                return ExpenseFragment.newInstance(context, position, vehicleReg, coordinatorLayout, keyboardView, numPad);
            }
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


}
