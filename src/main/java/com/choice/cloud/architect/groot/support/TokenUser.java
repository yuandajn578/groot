package com.choice.cloud.architect.groot.support;

import com.google.common.base.MoreObjects;


public class TokenUser {

    private String mobile;

    private String aid;

    private String uid;

    private String tid;

    private String sid;

    private String atoken;

    private String identityId;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAtoken() {
        return atoken;
    }

    public void setAtoken(String atoken) {
        this.atoken = atoken;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mobile", mobile)
                .add("aid", aid)
                .add("uid", uid)
                .add("tid", tid)
                .add("sid", sid)
                .add("atoken", atoken)
                .add("identityId", identityId)
                .omitNullValues()
                .toString();
    }
}
