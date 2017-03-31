package com.sunyie.android.trackdemo;

import com.sunyie.android.trackdemo.activity.BaseWrap;
import com.sunyie.android.trackdemo.entity.AliasResultEntity;
import com.sunyie.android.trackdemo.entity.LocationEntity;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by yukunlin on 2016/12/13.
 */

public class TrackAPI {
    public interface Track {
        //注册
        @GET("Track/register/name/{name}/password/{password}/phone/{phone}/company/{company}/job/{job}/appId/{appId}/portrait")
        Call<UserInfo> register(@Path("name") String name, @Path("password") String password, @Path("phone") String phone, @Path("company") String company, @Path("job") String job, @Path("appId") String appId);

        //登录
        @GET("Track/login/phone/{phone}/password/{password}/appId/{appId}")
        Call<UserInfo> login(@Path("phone") String phone, @Path("password") String password, @Path("appId") String appId);

        //发送位置
        @GET("Position/responsePosition/id/0/position/{position}/user_id/{user_id}/appid/{appid}")
        Call<BaseWrap> sendPosition(@Path("position") String position,
                                    @Path("user_id") String user_id,
                                    @Path("appid") String appid);

        //获取好友列表
        @GET("Track/friendList/user_id/{user_id}")
        Call<List<UserInfo>> getFriendsList(@Path("user_id") String user_id);

        //获取好友位置
        @GET("/Track/lastposition/appid/{appid}/user_id/{user_id}/id/{id}")
        Call<BaseWrap> getFriendsLocation(@Path("appid") String appid, @Path("user_id") String user_id, @Path("id") String id);

        //获取好友请求列表
        @GET("Track/getRequest/user_id/{user_id}/limit/12")
        Call<List<Request>> getRequest(@Path("user_id") String user_id);

        //搜索用户
        @GET("Track/searchuser/phone/{phone}/user_id/{user_id}")
        Call<UserInfo> searchUser(@Path("phone") String phone, @Path("user_id") String user_id);

        //发送请求添加好友
        @GET("Track/request/appid/{appid}/user_id/{user_id}/id/{id}")
        Call<BaseWrap> addFriends(@Path("appid") String appid, @Path("user_id") String user_id, @Path("id") String id);

        //同意添加好友
        @GET("Track/approve/appid/{appid}/user_id/{user_id}/id/{id}")
        Call<BaseWrap> approve(@Path("appid") String appid, @Path("user_id") String user_id, @Path("id") String id);

        //删除好友
        @GET("Track/friendDelete/appid/{appid}/id/{id}/user_id/{user_id}")
        Call<BaseWrap> deleteFriends(@Path("appid") String appid, @Path("user_id") String user_id, @Path("id") String id);

        //删除请求位置信息
        @GET("Track/deleteHistory/id/{id}")
        Call<BaseWrap> deleteLocationRequest(@Path("id") String itemId);

        //用户修改昵称
        @GET("Track/rename/name/{name}/user_id/{user_id}")
        Call<BaseWrap> rename(@Path("name") String name, @Path("user_id") String user_id);

        //退出
        @GET("Track/quit/user_id/{user_id}")
        Call<BaseWrap> quit(@Path("user_id") String user_id);

        //修改头像
        @Multipart
        @POST("Track/portrait/phone/{phone}")
        Call<BaseWrap> modifyAvatar(@Part MultipartBody.Part part,
                                    @Path("phone") String phone);

        //修改密码
        @GET("Track/modification/phone/{phone}/password/{password}")
        Call<BaseWrap> modifyPassword(@Path("phone") String phone,
                                      @Path("password") String password);
        //绑定别名
        @GET("/admin/os-php/bin_login.php?")
        Call<AliasResultEntity> LoginpostAlias(@Query("cid") String cid,
                                          @Query("name") String name);
        //获取实时位置
        @GET("/Track/lastposition/appid/{appid}/user_id/{user_id}/id/{id}/t/2")
        Call<LocationEntity> getFriendsNowLocation(@Path("appid") String appid,
                                                   @Path("user_id") String user_id,
                                                   @Path("id") String id);
    }

//    public static Track createApi(final String sessid) {
//        ClearableCookieJar cookieJar =
//                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MyApplication.getInstance().getApplicationContext()));
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
////                .addInterceptor(new Interceptor() {
////                    @Override
////                    public Response intercept(Chain chain) throws IOException {
////                        Request original = chain.request();
////                        Request request = original.newBuilder()
//////                                .header("Set-Cookie", sessid)
////                                .addHeader("Set-Cookie",sessid)
////                                .build();
////
////                        return chain.proceed(request);
////                    }
////                })
//                .build();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://api.sunsyi.com:8081/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
//                .build();
//        Track trackAPI = retrofit.create(Track.class);
//        return trackAPI;
//    }

    public static Track createApi() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.sunsyi.com:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        Track trackAPI = retrofit.create(Track.class);
        return trackAPI;
    }
    //绑定别名
    public static Track createApi2(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://www.sunyie.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Track track = retrofit.create(Track.class);
        return track;
    }
}
