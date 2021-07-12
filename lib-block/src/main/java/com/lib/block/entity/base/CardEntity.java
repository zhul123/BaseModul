package com.lib.block.entity.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡片实体类
 */
public class CardEntity<T> implements Serializable {
    /**
     * 卡片类型
     */
    private String type;
    /**
     * 样式
     */
    private StyleEntity style;
    /**
     * 卡片中视图数据集合（卡片body）
     */
    private List<ViewEntity<T>> items = new ArrayList<>();
    /**
     * 卡片头部（内部只能放视图，不可放卡片）
     */
    private ViewEntity header;
    /**
     * 卡片底部（内部只能放视图，不可放卡片）
     */
    private ViewEntity footer;

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

    public List<ViewEntity<T>> getItems() {
        return items;
    }

    public void setItems(List<ViewEntity<T>> items) {
        this.items = items;
    }

    public void setItems(ViewEntity viewEntity){
        items.add(viewEntity);
    }

    public ViewEntity getHeader() {
        return header;
    }

    public void setHeader(ViewEntity header) {
        this.header = header;
    }

    public ViewEntity getFooter() {
        return footer;
    }

    public void setFooter(ViewEntity footer) {
        this.footer = footer;
    }
}
