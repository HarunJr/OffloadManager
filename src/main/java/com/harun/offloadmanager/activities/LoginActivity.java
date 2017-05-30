package com.harun.offloadmanager.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.harun.offloadmanager.R;
import com.harun.offloadmanager.fragments.LoginFragment;
import com.harun.offloadmanager.fragments.RegisterFragment;
import com.harun.offloadmanager.models.User;
import com.harun.offloadmanager.service.UserServices;

public class LoginActivity extends BaseActivity implements LoginFragment.OnLoginFragmentInteractionListener, RegisterFragment.OnRegistrationFragmentInteractionListener{
    public static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.w(LOG_TAG, "LoginActivity");

        displayLoginFragment();
    }

    private void displayLoginFragment() {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_login, new LoginFragment())
                .commit();
    }

    @Override
    public void onLoginFragmentInteraction(User user) {
        Log.w(LOG_TAG, "loginUser " + user.getPhoneNo());
        bus.post(new UserServices.UserServerRequest("login", user));
    }

    @Override
    public void onRegistrationFragmentInteraction(User user) {
        bus.post(new UserServices.UserServerRequest("signup", user));
    }

    @Override
    public void onBackPressed() {
        //do nothing
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}
