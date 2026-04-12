package com.dbeast.templates_generator.app_settings;

public class AppPOJO{
    private String host;
    private int port;
    private String http_proxy;
    private String https_proxy;
    private String no_proxy;
    private String proxy_user;
    private String proxy_password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHttp_proxy() {
        return http_proxy;
    }

    public void setHttp_proxy(String http_proxy) {
        this.http_proxy = http_proxy;
    }

    public String getHttps_proxy() {
        return https_proxy;
    }

    public void setHttps_proxy(String https_proxy) {
        this.https_proxy = https_proxy;
    }

    public String getNo_proxy() {
        return no_proxy;
    }

    public void setNo_proxy(String no_proxy) {
        this.no_proxy = no_proxy;
    }

    public String getProxy_user() {
        return proxy_user;
    }

    public void setProxy_user(String proxy_user) {
        this.proxy_user = proxy_user;
    }

    public String getProxy_password() {
        return proxy_password;
    }

    public void setProxy_password(String proxy_password) {
        this.proxy_password = proxy_password;
    }
}