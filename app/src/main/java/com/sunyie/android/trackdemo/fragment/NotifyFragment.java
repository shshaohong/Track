package com.sunyie.android.trackdemo.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.Request;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.activity.BaseWrap;
import com.sunyie.android.trackdemo.adapter.AddFriendsAdapter;
import com.sunyie.android.trackdemo.adapter.RequestLocationAdapter;
import com.sunyie.android.trackdemo.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotifyFragment extends DialogFragment {
    @InjectView(R.id.friendsListView)
    SwipeMenuListView friendsListView;
    @InjectView(R.id.listView)
    ListView listView;
    @InjectView(R.id.addFriendsLayout)
    LinearLayout addFriendsLayout;
    @InjectView(R.id.rightImageView)
    ImageView rightImageView;
    @InjectView(R.id.rightImageView2)
    ImageView rightImageView2;
    private AddFriendsAdapter adapter;
    private RequestLocationAdapter requestLocationAdapter;
    private List<Request> data;
    private List<Request> requestData;
    private boolean friends;
    private boolean location;
    private static final String TAG = "NotifyFragment";
    private TrackAPI.Track api = TrackAPI.createApi();
    ;

    public NotifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notify, container, false);
        ButterKnife.inject(this, root);
        init();
        return root;
    }

    private void init() {
        api = TrackAPI.createApi();
        data = new ArrayList<>();
        adapter = new AddFriendsAdapter(getContext());
        friendsListView.setAdapter(adapter);
        requestData = new ArrayList<>();
        requestLocationAdapter = new RequestLocationAdapter(getContext());
        listView.setAdapter(requestLocationAdapter);
        requestLocationAdapter.setOnItemDeleteListener(new RequestLocationAdapter.OnItemDeleteListener() {
            @Override
            public void onDelete(Request request, final int position) {
                api.deleteLocationRequest(request.getIndex_id()).enqueue(new Callback<BaseWrap>() {
                    @Override
                    public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                        if (response.body().getStatus().equals("1")) {
                            Toast.makeText(getContext(), "delete success", Toast.LENGTH_SHORT).show();
                            requestData.remove(position);
                            requestLocationAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseWrap> call, Throwable t) {
                        Toast.makeText(getContext(), "network error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//                SwipeMenuItem deleteItem = new SwipeMenuItem(
//                        getContext());
//                // set item background
//                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//                        0x3F, 0x25)));
//                // set item width
//                deleteItem.setWidth(SimpleUtils.dip2px(getContext(), 90));
//                // set a icon
//                deleteItem.setIcon(R.drawable.ic_delete);
//                // add to menu
//                menu.addMenuItem(deleteItem);
//            }
//        };
        // set creator
//        listView.setMenuCreator(creator);
//        // other setting
//        listView.setCloseInterpolator(new BounceInterpolator());

//        Call<List<Request>> request = api.getRequest(MyApplication.getInstance().getUserInfo().getId());
//        request.enqueue(new Callback<List<Request>>() {
//            @Override
//            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
//                List<Request> requestList = response.body();
//                Collections.reverse(requestList);
//                for (int i = 0; i < requestList.size(); i++) {
//                    if (response.body().get(i).getTime() != null && !response.body().get(i).getTime().equals("")) {
//                        requestData.add(response.body().get(i));
//
//                    } else {
//                        data.add(response.body().get(i));
//
//                    }
//                }
//                requestLocationAdapter.setData(requestData);
//                requestLocationAdapter.notifyDataSetChanged();
//                adapter.setData(data);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Call<List<Request>> call, Throwable t) {
//
//            }
//        });

//        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        Call<BaseWrap> deleteLocationRequest = api.deleteLocationRequest(MyApplication.getInstance().getAppid(), MyApplication.getInstance().getUserInfo().getId());
//                        deleteLocationRequest.enqueue(new Callback<BaseWrap>() {
//                            @Override
//                            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
//                                if (response.body().getStatus().equals("1")) {
//                                    data.remove(position);
//                                    adapter.notifyDataSetChanged();
//                                    Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<BaseWrap> call, Throwable t) {
//
//                            }
//                        });
//
//                        break;
//                }
//            }
//        });

    }

    @OnClick(R.id.addFriendsLayout)
    void addFriendsClick() {
//        RotateAnimation rotateAnimation = new RotateAnimation(
//                0,180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (friends) {
            friendsListView.setVisibility(View.GONE);
            friends = false;
            RotateAnimation rotateAnimation = new RotateAnimation(
                    90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillAfter(true);//停在最后
            rotateAnimation.setDuration(150);
            rightImageView.startAnimation(rotateAnimation);
        } else {
            friendsListView.setVisibility(View.VISIBLE);
            friends = true;
            RotateAnimation rotateAnimation = new RotateAnimation(
                    0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillAfter(true);//停在最后
            rotateAnimation.setDuration(150);
            rightImageView.startAnimation(rotateAnimation);
        }
    }

    @OnClick(R.id.locationLayout)
    void locationClick() {
        if (location) {
            listView.setVisibility(View.GONE);
            location = false;
            RotateAnimation rotateAnimation = new RotateAnimation(
                    90, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillAfter(true);//停在最后
            rotateAnimation.setDuration(150);//所用时间
            rightImageView2.startAnimation(rotateAnimation);
        } else {
            listView.setVisibility(View.VISIBLE);
            location = true;
            RotateAnimation rotateAnimation = new RotateAnimation(
                    0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillAfter(true);//停在最后
            rotateAnimation.setDuration(150);
            rightImageView2.startAnimation(rotateAnimation);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    public void refresh() {
        Call<List<Request>> request = api.getRequest(MyApplication.getInstance().getUserInfo().getId());
        request.enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                List<Request> requestList = response.body();
                Collections.reverse(requestList);
                requestData.clear();
                data.clear();
                for (int i = 0; i < requestList.size(); i++) {
                    Log.e("request", requestList.get(i).getName());

                    if (response.body().get(i).getTime() != null && !response.body().get(i).getTime().equals("")) {
                        requestData.add(response.body().get(i));
                    } else {
                        data.add(response.body().get(i));
                    }
                }
                requestLocationAdapter.setData(requestData);
                requestLocationAdapter.notifyDataSetChanged();
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {

            }
        });
    }
}
