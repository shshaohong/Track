package com.sunyie.android.trackdemo.entity;

/**
 * Created by shaohong on 2017-3-10.
 */

public class LocationEntity {
    private String status;
    private String send;
    private String content;
    private String createtime;

    public String getSend() {
        return send;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
