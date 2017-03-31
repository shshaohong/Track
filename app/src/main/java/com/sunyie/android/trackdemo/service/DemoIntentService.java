package com.sunyie.android.trackdemo.service;

/**
 * Created by shaohong on 2017-3-6.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.activity.BaseWrap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class DemoIntentService extends GTIntentService{
    private LocationClient mLocationClient;
    private double mLatitude;
    private double mLongitude;
    public DemoIntentService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(getApplicationContext());
        // 第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开GPS
        option.setCoorType("bd09ll");// 设置百度坐标类型，默认gcj02，会有偏差，bd9ll百度地图坐标类型，将无偏差的展示到地图上
        option.setIsNeedAddress(true);// 需要地址信息
        mLocationClient.setLocOption(option);

        // 第三步，实现BDLocationListener接口
        mLocationClient.registerLocationListener(listener);

        // 第四步，开始定位
        mLocationClient.start();

    }
    private BDLocationListener listener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 如果没有拿到结果，重新请求：部分机型会失败
            if (bdLocation == null) {
                mLocationClient.requestLocation();
                return;
            }

            // 定位结果的经纬度
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();

            String position = mLongitude + ":"+ mLatitude;
            SharedPreferences sf = getSharedPreferences("location", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.putString("position", position);
            editor.commit();
            Log.e("ok获取经纬度：",  "经纬度:" + mLongitude + "====" + mLatitude);
        }
    };

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }
    /**
     * 透传消息
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.e("ok 透传收到信息:", msg.toString());
        StringBuilder builder = new StringBuilder();
        byte[] payload = msg.getPayload();
        for (byte pay : payload) {
            builder.append(pay);
        }

        String s = builder.toString();

        SharedPreferences sf = getSharedPreferences("location", Context.MODE_PRIVATE);
        String position = sf.getString("position", "");

        Log.e("ok透传消息外:", "OK" +   "经纬度:" + position+"......."+s);

        TrackAPI.Track track = TrackAPI.createApi();
            track.sendPosition(position, MyApplication.getInstance().getUserInfo().getId(),MyApplication.getInstance().getAppid()).enqueue(new Callback<BaseWrap>() {
                @Override
                public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                    if (response.body().getStatus().equals("1")) {
                        Log.e("ok发送实时位置成功", response.body().getMessage()+"...");
                    }
                }

                @Override
                public void onFailure(Call<BaseWrap> call, Throwable t) {
                    Log.e("发送实时位置失败", t.getMessage()+"...");
                }
            });
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        SharedPreferences sf = getSharedPreferences("clientId", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("cid", clientid);
        editor.commit();
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

}

