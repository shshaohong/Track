package com.sunyie.android.trackdemo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.UserInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPwdActivity extends BaseActivity {

    @InjectView(R.id.phoneNumberEditText)
    EditText phoneNumberEditText;
    @InjectView(R.id.verifyEditText)
    EditText verifyEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.confirmPwdEditText)
    EditText confirmPwdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.inject(this);
        init();
    }

    private boolean check() {
        if (phoneNumberEditText.getText().toString().length() == 0) {
            Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (verifyEditText.getText().toString().length() == 0) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordEditText.getText().toString().length() < 8) {
            Toast.makeText(this, "密码至少为9位", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!passwordEditText.getText().toString().equals(confirmPwdEditText.getText().toString())) {
            Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void init() {
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                        String userName = userNameEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();
                        String phone = phoneNumberEditText.getText().toString().trim();
                        final String appId = tm.getDeviceId() + "0";

                        final TrackAPI.Track api = TrackAPI.createApi();
                        api.modifyPassword(phone, password).enqueue(new Callback<BaseWrap>() {
                            @Override
                            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                                if (response.body().getStatus().equals("1")) {
                                    Toast.makeText(ResetPwdActivity.this, "密码重置成功", Toast.LENGTH_SHORT).show();
                                    final ProgressDialog progressDialog = new ProgressDialog(ResetPwdActivity.this);
                                    progressDialog.setMessage("正在登录");
                                    progressDialog.show();
                                    Call<UserInfo> login = api.login(phoneNumberEditText.getText().toString().trim(), passwordEditText.getText().toString().trim(), appId);
                                    login.enqueue(new Callback<UserInfo>() {
                                        @Override
                                        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                                            UserInfo body = response.body();
                                            if (body.getStatus().equals("1")) {
                                                progressDialog.dismiss();
                                                MyApplication.getInstance().setUserInfo(body);
                                                SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                Gson gson = new Gson();
                                                editor.putString("phone", body.getPhone());
                                                editor.putString("password", passwordEditText.getText().toString().trim());
                                                editor.putString("user", gson.toJson(response.body()));
                                                editor.commit();
                                                Intent intent = new Intent(ResetPwdActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(ResetPwdActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<UserInfo> call, Throwable t) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ResetPwdActivity.this,"访问网络出错",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(ResetPwdActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseWrap> call, Throwable t) {
                                Toast.makeText(ResetPwdActivity.this, "访问网络出错", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ResetPwdActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

    }

    @OnClick(R.id.commitBtn)
    void submit() {
        if (check()) {
            SMSSDK.submitVerificationCode("86", phoneNumberEditText.getText().toString().trim(),
                    verifyEditText.getText().toString().trim());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.getVerifyTextView)
    void getVerify() {
        Toast.makeText(ResetPwdActivity.this, "验证码获取中", Toast.LENGTH_SHORT).show();
        SMSSDK.getVerificationCode("86", phoneNumberEditText.getText().toString().trim());


    }

    @OnClick(R.id.backImageView)
    void backClick() {
        finish();
    }
}
