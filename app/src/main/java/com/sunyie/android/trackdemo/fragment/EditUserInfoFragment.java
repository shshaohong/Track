package com.sunyie.android.trackdemo.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.activity.BaseWrap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserInfoFragment extends DialogFragment {
    @InjectView(R.id.userName)
    EditText userName;
    private OnSaveListener onSaveListener;

    public void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public EditUserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_user_info, container, false);
        ButterKnife.inject(this, root);
        init();
        return root;
    }

    private void init() {
        userName.setText(MyApplication.getInstance().getUserInfo().getName());
    }

    @OnClick(R.id.save)
    void saveClick() {
        TrackAPI.Track api = TrackAPI.createApi();
        Call<BaseWrap> rename = api.rename(userName.getText().toString().trim(), MyApplication.getInstance().getUserInfo().getId());
        rename.enqueue(new Callback<BaseWrap>() {
            @Override
            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                if (response.body().getStatus().equals("1")) {
                    Toast.makeText(getContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    onSaveListener.onSave();
                    MyApplication.getInstance().getUserInfo().setName(userName.getText().toString().trim());
                } else {
                    Toast.makeText(getContext(), "修改失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseWrap> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.backImageView)
    void backClick() {
        dismiss();
    }

    public interface OnSaveListener {
        void onSave();
    }
}
