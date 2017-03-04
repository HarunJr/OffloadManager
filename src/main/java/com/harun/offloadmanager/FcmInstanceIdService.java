package com.harun.offloadmanager;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.harun.offloadmanager.data.LocalStore;

/**
 * Created by HARUN on 2/3/2017.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {
    public static final String LOG_TAG = FcmInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        LocalStore userLocalStore = new LocalStore(getApplicationContext());
        userLocalStore.storeToken(refreshedToken);
    }
}
