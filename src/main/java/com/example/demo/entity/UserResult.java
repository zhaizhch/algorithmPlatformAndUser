package com.example.demo.entity;

public class UserResult {
    private String authority;
    private String namespace;
    private String userName;
    private String userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserResult{" +
                "authority='" + authority + '\'' +
                ", namespace='" + namespace + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
