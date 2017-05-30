package com.harun.offloadmanager.live;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harun.offloadmanager.infrastructure.OffloadManagerApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HARUN on 12/18/2016.
 */

public class Module {
    public static final String LOG_TAG = Module.class.getSimpleName();

    public static void Register(OffloadManagerApplication application){
        new LiveOffloadManagerServices(application, createOffloadService());
    }

    private static OffloadWebServices createOffloadService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OffloadManagerApplication.ONLINE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Log.w(LOG_TAG, "OffloadWebServices " + retrofit.create(OffloadWebServices.class));

        return retrofit.create(OffloadWebServices.class);
    }
}
