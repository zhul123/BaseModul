package com.base.app;

import java.io.Serializable;

public class BaseBean implements Serializable {
    public String arouterUrl;

    public String getArouterUrl() {
        return arouterUrl;
    }

    public void setArouterUrl(String arouterUrl) {
        this.arouterUrl = arouterUrl;
    }
}
