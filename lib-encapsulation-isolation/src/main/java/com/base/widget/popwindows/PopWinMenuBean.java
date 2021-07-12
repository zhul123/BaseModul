package com.base.widget.popwindows;

import com.base.app.BaseBean;

public class PopWinMenuBean extends BaseBean {
    public enum PopMenuItemType{
        ADD,
        DEL,
        ORDER,
        SHARE,
        BACK,
        NOTEBOARD
    }

    public int imgRes;
    public String content;
    public PopMenuItemType itemType;


}
