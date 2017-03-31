package com.sunyie.android.trackdemo.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.MyService;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.activity.BaseWrap;
import com.sunyie.android.trackdemo.activity.LoginActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends DialogFragment {

    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.address)
    TextView address;
    @InjectView(R.id.email)
    TextView email;
    @InjectView(R.id.phone)
    TextView phone;
    @InjectView(R.id.avatar)
    RoundedImageView avatar;

    private static final String TAG = "NotifyFragment";
    private String[] items = new String[]{"选择本地图片", "打开相机拍照"};/* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 0, CAMERA_REQUEST_CODE = 1, RESULT_REQUEST_CODE = 2;
    private static final String IMAGE_FILE_NAME = "faceImage.jpg", SAVE_AVATORNAME = "tempImage.jpg";
    private String path;

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.inject(this, root);
        init();
        return root;
    }

    private void init() {
        userName.setText(MyApplication.getInstance().getUserInfo().getName());
        phone.setText(MyApplication.getInstance().getUserInfo().getPhone());
        String portrait = MyApplication.getInstance().getUserInfo().getPortrait();
        if (portrait.equals("")) {

        } else {
            Picasso.with(getContext()).load("http://api.sunsyi.com:8081/portrait/" + portrait).placeholder(R.drawable.default_avatar_male).into(avatar);
        }
    }

    @OnClick(R.id.loginOut)
    void loginOutClick() {

        TrackAPI.Track api = TrackAPI.createApi();
        Call<BaseWrap> quit = api.quit(MyApplication.getInstance().getUserInfo().getId());
        quit.enqueue(new Callback<BaseWrap>() {
            @Override
            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                if (response.body().getStatus().equals("1")) {
                    getActivity().stopService(new Intent(getActivity(), MyService.class));
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("track", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone", null);
                    editor.putString("password", null);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                } else {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("track", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone", null);
                    editor.putString("password", null);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<BaseWrap> call, Throwable t) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @OnClick(R.id.userName)
    void userNameClick() {
        EditUserInfoFragment fragment = new EditUserInfoFragment();
        fragment.setOnSaveListener(new EditUserInfoFragment.OnSaveListener() {
            @Override
            public void onSave() {
                userName.setText(MyApplication.getInstance().getUserInfo().getName());
            }
        });
        fragment.show(getFragmentManager(), "dialogFragment");
    }

    @OnClick(R.id.avatar)
    void avatarClick() {
        showPictureDialog();
    }

    private void showPictureDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(
                                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                                break;
                            case 1:
                                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                                }
                                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != getActivity().RESULT_CANCELED)
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File tempFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        Toast.makeText(getContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        setImageToView(data);
                    }
                    break;
            }
    }

    /**
     * Created by FuChen on 2014/8/18.
     */

    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪

        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * 保存裁剪之后的图片数据
     * Created by FuChen on 2014/8/18.
     */
    private void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap photo = null;
        if (extras != null) {
            photo = extras.getParcelable("data");
        } else {
            Uri uri = data.getData();
            try {
                photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (photo != null) {
            path = saveMyBitmap(photo);//保存图片操作
            avatar.setImageBitmap(photo);
            uploadAvatar();
        }
    }

    /**
     * Created by FuChen on 2014/8/18.
     */
    private String saveMyBitmap(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/BookScan/");
        if (!dir.exists())
            dir.mkdir();
        File f = new File(dir.getPath() + SAVE_AVATORNAME);
        try {
            f.createNewFile();
            FileOutputStream fOut = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadAvatar() {
        if (path == null)
            return;
        File file = new File(path);//filePath 图片地址
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imageBodyPart = MultipartBody.Part.createFormData("portrait", file.getName(), imageBody);
        TrackAPI.Track api = TrackAPI.createApi();
        api.modifyAvatar(imageBodyPart, MyApplication.getInstance().getUserInfo().getPhone()).enqueue(new Callback<BaseWrap>() {
            @Override
            public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                if (response.body().getStatus().equals("1")) {
                    Toast.makeText(getContext(), "upload avatar success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "upload avatar failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseWrap> call, Throwable t) {
                Toast.makeText(getContext(), "访问网络出错", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
