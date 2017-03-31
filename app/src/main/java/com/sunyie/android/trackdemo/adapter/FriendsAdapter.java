package com.sunyie.android.trackdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.UserInfo;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yukunlin on 2016/12/8.
 */

public class FriendsAdapter extends BaseAdapter {
    private Context context;
    private List<UserInfo> data;

    public FriendsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<UserInfo> data) {
        this.data = data;
    }

    public List<UserInfo> getData() {
        return data;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_friends, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        UserInfo userInfo = data.get(i);
        if (userInfo.getPortrait() != null && !userInfo.getPortrait().equals("")) {
            Picasso.with(context).load("http://api.sunsyi.com:8081/portrait/" + userInfo.getPortrait())
                    .placeholder(R.drawable.default_avatar_male).into(viewHolder.avatar);
        } else {
            viewHolder.avatar.setImageResource(R.drawable.default_avatar_male);
        }
        viewHolder.userName.setText(userInfo.getName());
        return view;
    }

    class ViewHolder {
        @InjectView(R.id.avatar)
        ImageView avatar;
        @InjectView(R.id.name)
        TextView userName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
