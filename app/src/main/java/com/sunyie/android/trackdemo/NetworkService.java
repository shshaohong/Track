package com.sunyie.android.trackdemo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yukunlin on 2016/12/13.
 */

public class NetworkService {
    public static TrackAPI createApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.sunsyi.com:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TrackAPI trackAPI = retrofit.create(TrackAPI.class);
        return trackAPI;
    }
}
