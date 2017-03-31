package com.sunyie.android.trackdemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.squareup.picasso.Picasso;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.Utils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsLocationActivity extends AppCompatActivity {

    @InjectView(R.id.mapView)
    MapView mapView;

    private String[] items;
    private String[] position;

    private String upDate;
    private BaiduMap mBaiduMap;
    private String mUserPortrait;
    private String mUserName;
    private String mAppid;
    private String mId;

    private BaiduMap.OnMarkerClickListener mOnMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            Toast.makeText(FriendsLocationActivity.this, "更新时间：" + upDate, Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    private Timer mTimer;
    private LatLng mPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_friends_location);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        mAppid = getIntent().getStringExtra("appid");
        mId = getIntent().getStringExtra("id");
        mUserName = getIntent().getStringExtra("userName");
        mUserPortrait = getIntent().getStringExtra("userPortrait");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在获取位置...");
        progressDialog.show();
        TrackAPI.Track api = TrackAPI.createApi();
        api.getFriendsLocation(mAppid, MyApplication.getInstance().getUserInfo().getId(), mId).enqueue(new Callback<BaseWrap>() {
            @Override
            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                progressDialog.cancel();
                if (response.body().getStatus().equals("1")) {
                    position = response.body().getContent().split(":");

                    upDate = response.body().getCreatetime();
                    SharedPreferences sf = getSharedPreferences("pos", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString("pos0", position[0] + ":" + position[1]);
                    editor.commit();
                    setPosition();
                }else {
                    SharedPreferences sf = getSharedPreferences("pos", Context.MODE_PRIVATE);
                    String pos0 = sf.getString("pos0", "");
                    position = pos0.split(":");
                }
            }

            @Override
            public void onFailure(Call<BaseWrap> call, Throwable t) {
                SharedPreferences sf = getSharedPreferences("pos", Context.MODE_PRIVATE);
                String pos0 = sf.getString("pos0", "");
                position = pos0.split(":");

                progressDialog.cancel();
            }
        });
    }

    private void setPosition() {
        //设置用户头像显示标注物里
        Picasso.with(this).load("http://api.sunsyi.com:8081/portrait/" + mUserPortrait);

        mBaiduMap = mapView.getMap();//拿到百度地图操作类
        //定义Maker坐标点  	113.418491:23.119634
        mPoint = new LatLng(Double.parseDouble(position[1]), Double.parseDouble(position[0]));
        //构建Marker图标


        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(mPoint)
                .icon(bitmap);

        //在地图上添加Marker，并显示
        mapView.getMap().addOverlay(option);
        //设置标注物监听
        mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);

        MapStatus mMapStatus = new MapStatus.Builder()
                .target(mPoint)
                .rotate(0)
                .overlook(0)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mapView.getMap().setMapStatus(mMapStatusUpdate);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setTimer();
//        setPosition();
        mapView.onResume();
    }

    private void setTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateLocation();
            }
        }, 0, 5000);
    }

    private void updateLocation() {
        TrackAPI.Track api = TrackAPI.createApi();
        api.getFriendsLocation(mAppid, MyApplication.getInstance().getUserInfo().getId(), mId)
                .enqueue(new Callback<BaseWrap>() {
                    @Override
                    public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                        if (response.body().getStatus().equals("1")) {
                            position = response.body().getContent().split(":");
                            upDate = response.body().getCreatetime();
                            mPoint = new LatLng(Double.parseDouble(position[1]), Double.parseDouble(position[0]));

//                            Toast.makeText(FriendsLocationActivity.this, "刷新位置", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseWrap> call, Throwable t) {

                    }
                });
    }


    @OnClick(R.id.go)
    void goClick() {

        if (Utils.isInstallByRead("com.autonavi.minimap") && Utils.isInstallByRead("com.baidu.BaiduMap")) {
            items = new String[]{"使用百度地图导航", "使用高德地图导航"};
        } else if (Utils.isInstallByRead("com.baidu.BaiduMap")) {
            items = new String[]{"使用百度地图导航"};
        } else if (Utils.isInstallByRead("com.autonavi.minimap")) {
            items = new String[]{"使用高德地图导航"};
        } else {
            Toast.makeText(this, "您尚未安装地图", Toast.LENGTH_LONG).show();
            Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return;
        }


        new AlertDialog.Builder(this)
                .setTitle("导航")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (items[0].equals("使用高德地图导航")) {
                                    Utils.goToNaviActivity(FriendsLocationActivity.this, "test", null, position[1], position[0], "1", "2");
                                } else {
//                                    Utils.goToNaviActivity(FriendsLocationActivity.this, "test", null, "23.115652", "113.422681", "1", "2");
                                }
                                break;
                            case 1:
                                Utils.goToNaviActivity(FriendsLocationActivity.this, "test", null, "23.115652", "113.422681", "1", "2");
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        mTimer.cancel();
    }



    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @OnClick(R.id.backImageView)
    void backClick() {
        finish();
    }
}
