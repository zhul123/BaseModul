package com.lib.block.entity.base;

import java.io.Serializable;

/**
 * 视图实体类
 */
public class ViewEntity<T> implements Serializable {
    /**
     * 视图类型
     */
    private String type;
    /**
     * 视图样式
     */
    private StyleEntity style = new StyleEntity();
    /**
     * 视图数据集合
     */
    private T datas;
    /**
     * 事件实体
     */
    private EventEntity event;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StyleEntity getStyle() {
        return style;
    }

    public void setStyle(StyleEntity style) {
        this.style = style;
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
}
