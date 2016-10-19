package com.harun.offloadmanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    public static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.w(LOG_TAG, "LoginActivity");

        displayLoginFragment();
    }

    private void displayLoginFragment(){

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_login, new LoginFragment())
                .commit();
    }

}
