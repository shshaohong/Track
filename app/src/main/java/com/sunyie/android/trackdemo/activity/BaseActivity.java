package com.sunyie.android.trackdemo.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sunyie.android.trackdemo.R;

import cn.smssdk.SMSSDK;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
//        SMSSDK.initSDK(this, "19a6a7588a412", "e7fe53744054251753423722e1b01948");
        SMSSDK.initSDK(this, "1ac9e46831c0e", "090ce7667a4e6cc364c6a15a5901e434");
    }
}
