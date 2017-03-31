package com.sunyie.android.trackdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.UserInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StartupActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null);
        String password = sharedPreferences.getString("password", null);
        if (phone != null && password != null) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String id = tm.getDeviceId() + "000000000000000000000000000000";
            String trueId = id.substring(0, 16);
            TrackAPI.Track api = TrackAPI.createApi();
            Call<UserInfo> login = api.login(phone, password, trueId);
            login.enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                    UserInfo userInfo = response.body();
                    if (userInfo.getStatus().equals("1")) {
                        MyApplication.getInstance().setUserInfo(userInfo);

                        SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        Gson gson = new Gson();
                        edit.putString("user", gson.toJson(response.body()));
                        edit.apply();

                        Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<UserInfo> call, Throwable t) {
                    SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(sharedPreferences.getString("user", "[]"), new TypeToken<UserInfo>() {
                    }.getType());
                    MyApplication.getInstance().setUserInfo(userInfo);
                    Intent intent = new Intent(StartupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000 * 1);
        }
    }
}
