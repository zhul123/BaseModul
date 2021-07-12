package com.lib.block.style;

import java.io.Serializable;

public class CardType implements Serializable {
    public static final String CONTAINER_ONECOLUMN = "container-oneColumn";
    public static final String CONTAINER_TWOCOLUMN = "container-twoColumn";
    public static final String CONTAINER_THREECOLUMN = "container-threeColumn";
    public static final String CONTAINER_FOURCOLUMN = "container-fourColumn";
    public static final String CONTAINER_FIVECOLUMN = "container-fiveColumn";
    //一行N列
    public static final String CONTAINER_FLOW = "container-flow";
    //一拖N
    public static final String CONTAINER_ONEPLUSN = "container-onePlusN";
    //悬浮布局，自动吸边
    public static final String CONTAINER_FLOAT = "container-float";
    //固定顶部或者底部，根据属性指定
    public static final String CONTAINER_FIX = "container-fix";
    //滚动固定(滚动到某个布局的时候，出现并固定)
    public static final String CONTAINER_SCROLLFIX = "container-scrollFix";
    //吸顶或吸底，根据属性指定
    public static final String CONTAINER_STICKY = "container-sticky";
    //banner
    public static final String CONTAINER_BANNER = "container-banner";
    //线性滚动，不像轮播一样具有一页一页的效果
    public static final String CONTAINER_SCROLL = "container-scroll";
    //瀑布流
    public static final String CONTAINER_WATERFALL = "container-waterfall";
}
