package com.sunyie.android.trackdemo.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.Toast;

import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.SimpleUtils;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.UserInfo;
import com.sunyie.android.trackdemo.activity.AddFriendsActivity;
import com.sunyie.android.trackdemo.activity.BaseWrap;
import com.sunyie.android.trackdemo.activity.FriendsLocationActivity;
import com.sunyie.android.trackdemo.adapter.FriendsAdapter;
import com.sunyie.android.trackdemo.swipemenulistview.SwipeMenu;
import com.sunyie.android.trackdemo.swipemenulistview.SwipeMenuCreator;
import com.sunyie.android.trackdemo.swipemenulistview.SwipeMenuItem;
import com.sunyie.android.trackdemo.swipemenulistview.SwipeMenuListView;

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
public class FriendsFragment extends DialogFragment {
    @InjectView(R.id.listView)
    SwipeMenuListView listView;

    private FriendsAdapter adapter;
    private List<UserInfo> data;
    private static final String TAG = "FriendsFragment";
    private TrackAPI.Track api = TrackAPI.createApi();
    private String[] items = new String[]{"查看位置","查看实时位置"};

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.inject(this, root);
        init();
        return root;
    }

    private void init() {
        adapter = new FriendsAdapter(getContext());
        listView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(SimpleUtils.dip2px(getContext(), 90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        listView.setMenuCreator(creator);
        // other setting
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //获取好友位置信息
                Intent intent = new Intent(getActivity(), FriendsLocationActivity.class);
                intent.putExtra("appid", data.get(i).getAppId());
                intent.putExtra("id",data.get(i).getId());
                intent.putExtra("userName", data.get(i).getName());
                intent.putExtra("userPortrait", data.get(i).getPortrait());
                getActivity().startActivity(intent);
            }
        });


        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Call<BaseWrap> deleteFriends = api.deleteFriends(MyApplication.getInstance().getAppid(), MyApplication.getInstance().getUserInfo().getId(), data.get(position).getId());
                        deleteFriends.enqueue(new Callback<BaseWrap>() {
                            @Override
                            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                                if (response.body().getStatus().equals("1")) {
                                    data.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "删除好友成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "删除好友失败", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseWrap> call, Throwable t) {

                            }
                        });

                        break;
                }
            }
        });
    }

    @OnClick(R.id.addImageView)
    void addClick() {
        Intent intent = new Intent(getActivity(), AddFriendsActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    public void refresh() {
        Call<List<UserInfo>> friendsList = api.getFriendsList(MyApplication.getInstance().getUserInfo().getId());
        friendsList.enqueue(new Callback<List<UserInfo>>() {
            @Override
            public void onResponse(Call<List<UserInfo>> call, Response<List<UserInfo>> response) {
                data = response.body();
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<UserInfo>> call, Throwable t) {

            }
        });
    }
}
