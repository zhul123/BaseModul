package com.lib.block.entity.base;

import java.io.Serializable;

/**
 * 选择实体类
 */
public class ChooseViewEntity implements Serializable {
    private String key;
    private String value;
    private String text;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
