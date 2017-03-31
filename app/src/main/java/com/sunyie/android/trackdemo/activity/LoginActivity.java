package com.sunyie.android.trackdemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.UserInfo;
import com.sunyie.android.trackdemo.service.DemoIntentService;
import com.sunyie.android.trackdemo.service.DemoPushService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.phoneNumberEditText)
    EditText phoneNumberEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.showPasswordImageView)
    ImageView showPasswordImageView;


    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        // com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

    }

    @OnClick(R.id.loginButton)
    void loginClick() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        final String phone = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String id = tm.getDeviceId() + "000000000000000000000000000000";
        String trueId = id.substring(0, 16);
        if (phone.length() == 0 || password.length() == 0) {
            Toast.makeText(this, "请输入账号或密码", Toast.LENGTH_SHORT).show();
            return;
        }
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://api.sunsyi.com:8081/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        TrackAPI trackAPI = retrofit.create(TrackAPI.class);
//        Call<UserInfo> login = trackAPI.login(phone, password, trueId);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录...");
        progressDialog.show();
        TrackAPI.Track api = TrackAPI.createApi();
        Call<UserInfo> login = api.login(phone, password, trueId);
        login.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                progressDialog.dismiss();
                UserInfo userInfo = response.body();
                if (userInfo.getStatus().equals("1")) {
                    MyApplication.getInstance().setUserInfo(userInfo);
                    SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userInfo.getId());
                    editor.putString("phone", userInfo.getPhone());
                    editor.putString("password", passwordEditText.getText().toString().trim());
                    editor.putString("user", gson.toJson(response.body()));
                    editor.commit();
//                    startService(new Intent(LoginActivity.this, Service1.class));

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "访问网络异常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.forgetPwdTextView)
    void forgetPwdClick() {
        Intent intent = new Intent(LoginActivity.this, ResetPwdActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.showPasswordImageView)
    void showPwdClick() {

    }

    @OnClick(R.id.registerTextView)
    void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
