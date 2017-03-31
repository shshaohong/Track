package com.sunyie.android.trackdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.Request;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yukunlin on 2016/12/12.
 */

public class RequestLocationAdapter extends BaseAdapter {
    private Context context;
    private List<Request> data;
    private OnItemDeleteListener onItemDeleteListener;

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        this.onItemDeleteListener = onItemDeleteListener;
    }

    public RequestLocationAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemDeleteListener {
        void onDelete(Request request, int position);
    }
    public List<Request> getData() {
        return data;
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
        if (i == 0) {
            ViewHolderHead holderHead = null;
            if (view != null && view.getTag() instanceof ViewHolderHead) {
                holderHead = (ViewHolderHead) view.getTag();

            } else {
                view = LayoutInflater.from(context).inflate(R.layout.item_request_head, viewGroup, false);
                holderHead = new ViewHolderHead(view);
                view.setTag(holderHead);
            }
            final Request request = data.get(i);
            final Date date = new Date(Long.parseLong(request.getTime() + "000"));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(date);
            holderHead.time.setText(format);
            holderHead.content.setText(request.getName() + "正在查看你的位置");
            holderHead.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemDeleteListener.onDelete(request, i);
                }
            });
        } else {
            ViewHolder holder = null;
            if (view != null && view.getTag() instanceof ViewHolder) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.item_request_normal, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            final Request request = data.get(i);
            Date date = new Date(Long.parseLong(request.getTime() + "000"));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(date);
            holder.time.setText(format);
            holder.content.setText(request.getName() + "正在查看你的位置");
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemDeleteListener.onDelete(request, i);
                }
            });
        }
        return view;
    }

    class ViewHolderHead {
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.time)
        TextView time;
        @InjectView(R.id.btnDelete)
        Button btnDelete;

        public ViewHolderHead(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class ViewHolder {
        @InjectView(R.id.content)
        TextView content;
        @InjectView(R.id.time)
        TextView time;
        @InjectView(R.id.btnDelete)
        Button btnDelete;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
