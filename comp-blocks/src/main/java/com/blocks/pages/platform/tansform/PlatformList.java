package com.blocks.pages.platform.tansform;

import java.io.Serializable;
import java.util.List;

public class PlatformList implements Serializable {
    public List<PlatformBean> data;

    public List<PlatformBean> getData() {
        return data;
    }

    public void setData(List<PlatformBean> data) {
        this.data = data;
    }
}
