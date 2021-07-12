package com.blocks.pages.platform.event;

import com.blocks.pages.platform.tansform.PlatformBean;
import com.tmall.wireless.tangram.structure.BaseCell;

public class PlatformEvent {
    public EventType eventType;
    public PlatformBean bean;
    public BaseCell cell;
    public int position;
    public enum EventType{
        ADD,
        DEL,
        EDIT
    }
}
