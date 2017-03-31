package com.sunyie.android.trackdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.Service1;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.entity.AliasResultEntity;
import com.sunyie.android.trackdemo.fragment.FriendsFragment;
import com.sunyie.android.trackdemo.fragment.NotifyFragment;
import com.sunyie.android.trackdemo.fragment.PersonalFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Fragment[] fragments;
    private int currentTabIndex;
    private FriendsFragment friendsFragment;
    private NotifyFragment notifyFragment;
    private PersonalFragment personalFragment;
    private int index;
    public static final String MY_RECEIVER = "MainActivity";

    @InjectView(R.id.friends)
    LinearLayout friends;
    @InjectView(R.id.notify)
    LinearLayout notify;
    @InjectView(R.id.personal)
    LinearLayout personal;

    //极光推送下来的消息模拟输出
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String bundle = intent.getStringExtra("bundle");
            Toast.makeText(context, bundle, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        Intent intent2 = new Intent(this, Service1.class);
        startService(intent2);

        IntentFilter intentFilter = new IntentFilter(MY_RECEIVER);
        init();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void init() {
        friends.setOnClickListener(this);
        notify.setOnClickListener(this);
        personal.setOnClickListener(this);
        friends.setSelected(true);
        friendsFragment = new FriendsFragment();
        notifyFragment = new NotifyFragment();
        personalFragment = new PersonalFragment();
        fragments = new Fragment[]{friendsFragment, notifyFragment, personalFragment};
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, friendsFragment)
                .show(friendsFragment)
                .commit();
        friendsFragment.refresh();

        SharedPreferences sf = getSharedPreferences("clientId", Context.MODE_PRIVATE);
        String cid = sf.getString("cid", null);
        TrackAPI.Track track = TrackAPI.createApi2();
        SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null);
        track.LoginpostAlias(cid,phone).enqueue(new Callback<AliasResultEntity>() {
            @Override
            public void onResponse(Call<AliasResultEntity> call, Response<AliasResultEntity> response) {
                if (response.body().getOk().equals("1")) {
                    Log.e("LoginActivity:", "绑定别名成功:"+response.body().getName());
                }else {
                    Log.e("LoginActivity:", "绑定别名失败");
                }
            }

            @Override
            public void onFailure(Call<AliasResultEntity> call, Throwable t) {
                Log.e("绑定别名失败", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal:
                index = 2;
                notify.setSelected(false);
                friends.setSelected(false);
                personal.setSelected(true);

                break;
            case R.id.notify:
                index = 1;
                personal.setSelected(false);
                friends.setSelected(false);
                notify.setSelected(true);
                notifyFragment.refresh();
                break;
            case R.id.friends:
                index = 0;
                notify.setSelected(false);
                friends.setSelected(true);
                personal.setSelected(false);
                friendsFragment.refresh();
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                ft.add(R.id.fragment_container, fragments[index]);
            }
            ft.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }


}
