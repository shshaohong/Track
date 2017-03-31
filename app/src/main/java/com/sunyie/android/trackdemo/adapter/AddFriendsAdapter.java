package com.sunyie.android.trackdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.Request;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.UserInfo;
import com.sunyie.android.trackdemo.activity.BaseWrap;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yukunlin on 2016/12/8.
 */

public class AddFriendsAdapter extends BaseAdapter {
    private Context context;
    private List<Request> data;

    public AddFriendsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Request> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_add_friends, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Picasso.with(context).load(R.drawable.default_avatar_male).into(viewHolder.avatar);
        viewHolder.userName.setText(data.get(i).getName() + "请求添加你为好友");
        viewHolder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrackAPI.Track api = TrackAPI.createApi();
                Call<BaseWrap> approve = api.approve(MyApplication.getInstance().getAppid(), MyApplication.getInstance().getUserInfo().getId(), data.get(i).getId());
                approve.enqueue(new Callback<BaseWrap>() {
                    @Override
                    public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                        if (response.body().getStatus().equals("1")) {
                            Toast.makeText(context, "添加好友成功", Toast.LENGTH_SHORT).show();
                            data.remove(i);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "添加好友失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseWrap> call, Throwable t) {

                    }
                });
            }
        });
        return view;
    }

    class ViewHolder {
        @InjectView(R.id.avatar)
        ImageView avatar;
        @InjectView(R.id.name)
        TextView userName;
        @InjectView(R.id.approve)
        TextView approve;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
