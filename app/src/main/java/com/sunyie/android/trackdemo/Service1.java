package com.sunyie.android.trackdemo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.sunyie.android.trackdemo.activity.BaseWrap;
import com.sunyie.android.trackdemo.service.DemoIntentService;
import com.sunyie.android.trackdemo.service.DemoPushService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 * <p>
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service {
    private static final String TAG = "Service1";
    private String longitude;
    private String latitude;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    public Service1() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..
        init();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        //为第三方自定义推送服务
        PushManager.getInstance().initialize(this, DemoPushService.class);
        //为第三方自定义的推送服务事件接收类
        PushManager.getInstance().registerPushIntentService(this, DemoIntentService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient.start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //读者可以修改此处的Minutes从而改变提醒间隔时间
        //此处是设置每隔90分钟启动一次
        //这是90分钟的毫秒数
        int Minutes = 30 * 60 * 1000;//30 *
        //SystemClock.elapsedRealtime()表示1970年1月1日0点至今所经历的时间
        long triggerAtTime = System.currentTimeMillis() + Minutes;
        //此处设置开启AlarmReceiver这个Service
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起，并且会唤醒CPU。
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        Toast.makeText(this, "启动了服务", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    private void init() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span = 15 * 60 * 1000;
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            Log.e("ba...............", latitude + ":" + longitude);
            bdLocation.getLocType();
            TrackAPI.Track api = TrackAPI.createApi();
            String position = longitude + ":" + latitude;
            SharedPreferences sharedPreferences = getSharedPreferences("track", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            UserInfo userInfo = gson.fromJson(sharedPreferences.getString("user", null), new TypeToken<UserInfo>() {
            }.getType());
            if (userInfo == null) {
                mLocationClient.stop();
                return;
            }
            Call<BaseWrap> sendPosition = api.sendPosition(position, userInfo.getId(),MyApplication.getInstance().getAppid());
            sendPosition.enqueue(new Callback<BaseWrap>() {
                @Override
                public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                    if (response.body().getStatus().equals("1")) {
                        Log.d(TAG, "onResponse: " + response.body().toString());
                        mLocationClient.stop();
                    }
                }

                @Override
                public void onFailure(Call<BaseWrap> call, Throwable t) {
                    mLocationClient.stop();
                }
            });
        }

    }
}
