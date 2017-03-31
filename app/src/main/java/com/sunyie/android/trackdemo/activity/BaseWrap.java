package com.sunyie.android.trackdemo.activity;

/**
 * Created by yukunlin on 2016/12/6.
 */

public class BaseWrap {
    private String status;
    private String message;
    private String content;
    private String createtime;


    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BaseWrap{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
