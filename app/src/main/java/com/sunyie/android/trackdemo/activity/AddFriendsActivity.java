package com.sunyie.android.trackdemo.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sunyie.android.trackdemo.Contact;
import com.sunyie.android.trackdemo.MyApplication;
import com.sunyie.android.trackdemo.R;
import com.sunyie.android.trackdemo.TrackAPI;
import com.sunyie.android.trackdemo.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendsActivity extends BaseActivity {
    private static final String TAG = "AddFriendsActivity";
    private final int REQUEST_CONTACT = 0;
    @InjectView(R.id.phoneNumberEditText)
    EditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.contactsLayout)
    void contactsClick() {
        List<Contact> contacts = getContacts();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACT);

    }

    public List<Contact> getContacts() {
        List<Contact> list = new ArrayList<>();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
// 	Cursor cursor = managedQuery(uri, projection, null, null, sortOrder); // 4.2.2不建议使用这种方式
        Cursor cursor = getContentResolver().query(uri, projection, null, null, sortOrder);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact man = new Contact();
            man.setName(name);
            man.setMobile(String.valueOf(phoneNum.trim().replace(" ", "").replace("+", "")));

            list.add(man);
        }
        cursor.close();
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONTACT:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    //处理返回的data,获取选择的联系人信息
                    Uri uri = data.getData();
                    String[] contacts = getPhoneContacts(uri);
                    phoneNumberEditText.setText(contacts[1]);

                    if (Build.VERSION.SDK_INT < 14) {

                    }
                }

                break;
        }

    }

    private String[] getPhoneContacts(Uri uri) {

        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
            if (phone != null) {
//                phone.moveToFirst();
                if (phone.moveToFirst()) {
                    contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }else {
                    Toast.makeText(this, "获取失败，该联系人号码为空", Toast.LENGTH_SHORT).show();
                }
            }
            if (Build.VERSION.SDK_INT < 14) {
                phone.close();
            }
        } else {
            return null;
        }
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
        return contact;
    }


    @OnClick(R.id.addLayout)
    void addClick() {
        final TrackAPI.Track api = TrackAPI.createApi();
        Call<UserInfo> userInfoCall = api.searchUser(phoneNumberEditText.getText().toString().trim(), MyApplication.getInstance().getUserInfo().getId());
        userInfoCall.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.body().getStatus().equals("0")) {
                    return;
                }
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                Call<BaseWrap> addFriends = api.addFriends(tm.getDeviceId() + "0", MyApplication.getInstance().getUserInfo().getId(), response.body().getId());
                addFriends.enqueue(new Callback<BaseWrap>() {
                    @Override
                    public void onResponse(Call<BaseWrap> call, Response<BaseWrap> response) {
                        if (response.body().getStatus().equals("0")) {
                            Toast.makeText(AddFriendsActivity.this, "已经发送请求，请勿重复", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getStatus().equals("1")) {
                            Toast.makeText(AddFriendsActivity.this, "请求成功", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getStatus().equals("2")) {
                            Toast.makeText(AddFriendsActivity.this, "此用户已经是您的好友了", Toast.LENGTH_SHORT).show();
                        } else if (response.body().getStatus().equals("3")) {
                            Toast.makeText(AddFriendsActivity.this, "用户已拒绝您的请求", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddFriendsActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseWrap> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.backImageView)
    void backClick() {
        finish();
    }
}





