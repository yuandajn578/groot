package com.choice.cloud.architect.groot.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TokenInfo {

    @JsonProperty("user_name")
    private String userName;

    private Integer exp;

    private List<String> authorities;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}