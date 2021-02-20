package com.blocks.views.floorviews;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.blocks.views.R;
import com.blocks.views.base.BaseFloorView;
import com.blocks.views.utils.ParamsUtils;
import com.tmall.wireless.tangram.structure.BaseCell;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.Iterator;


/**
 * @Auter zhulei
 * 图片文字上下布局模板
 * radius：圆角框
 * textColor:字体颜色
 * textSize:字体大小
 * imgWidth:图片宽度
 * imgHeight:图片高度
 * imgUrl:图片地址
 * text:标题描述
 */
public class ImageTextFloorView extends BaseFloorView<LinearLayout.LayoutParams> {
    //    ------功能图标参数----------
    protected static final String APPID = "appId";//
    protected static final String APPNAME = "text";//
    protected static final String APPTYPE = "appType";//
    protected static final String ISOLD = "isOld";//
    protected static final String ISWX = "isWx";//
    protected static final String LOGOURL = "imgUrl";//
    protected static final String SORT = "sort";//
    protected static final String TYPE = "type";//
    protected static final String AROUTERURL = "arouterUrl";//
    protected static final String URL = "url";//
    private ImageView iv_img;
    private TextView tv_title;
    private int DEFALUTWAH = dpToPx(45);//默认图片宽高
    private LinearLayout.LayoutParams mParams;

    public ImageTextFloorView(Context context) {
        super(context);
    }
    SharedPreferences sharedPreferences;
    @Override
    public void init() {
        sharedPreferences = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.floor_imagetext, null);
        if (view != null) {
            addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        iv_img = findViewById(R.id.iv_img);
        tv_title = findViewById(R.id.tv_title);
        mParams = (LinearLayout.LayoutParams) iv_img.getLayoutParams();
    }

    @Override
    public void onItemClick() {
        if (mBaseCell == null)
            return;
       /* FunctionItem item = new FunctionItem();;

        item.appId = getOptString(APPID);
        item.appName = getOptString(APPNAME);
        item.appType = getOptString(APPTYPE);
        item.isOld = getOptString(ISOLD);
        item.isWx = getOptString(ISWX);
        item.logoUrl = getOptString(LOGOURL);
        item.sort = getOptString(SORT);
        item.type = getOptString(TYPE);
        item.url = getOptString(URL);
        String arouterUrl = getOptString(AROUTERURL);

        //判断地址是否为空，是否包含协议头
        if (!TextUtils.isEmpty(arouterUrl)) {
            try {
                if (arouterUrl.indexOf(ARouterUtils.AROUTERRULE) == -1) {
                    AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(com.component.R.string.err_path_empty);
                    return;
                }
                arouterUrl = arouterUrl.replaceFirst(ARouterUtils.AROUTERRULE, "");
                arouterUrl = complexUrl(arouterUrl,getOptJsonObj(DATAS));
                System.out.println("=========arouterUrl:"+arouterUrl);
                Uri uri = Uri.parse(arouterUrl);

                ARouter.getInstance().build(uri)
                        .withSerializable(CommonProvider.CommonConstantDef.WEBVIEW_FUNCTIONITEM, item).navigation(getContext());
//                complexPostCard(postcard,getOptJsonObj(DATAS)).navigation(getContext());

            }catch (Exception e){
                AppToast.getInstance((Application) mContext.getApplicationContext()).makeText(com.component.R.string.err_path_empty);
            }

        }

*/
    }

    private Postcard complexPostCard(Postcard postcard, JSONObject datas){
        if(postcard == null || datas == null)
            return postcard;
        try {
            Iterator it = datas.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if(ParamsUtils.IMGURL.equals(key) || AROUTERURL.equals(key))
                    continue;
                postcard.withString(key,datas.getString(key));
            }
        }catch (Exception e) {
        }

        return postcard;
    }


    /**
     * 组合Url
     * @param url
     * @param datas
     */
    private String complexUrl(String url, JSONObject datas){
        if(TextUtils.isEmpty(url) || datas == null)
            return url;
        try {
            StringBuffer stringBuffer = new StringBuffer(url);
            Iterator it = datas.keys();
            while (it.hasNext()) {
                String key = it.next().toString();
                if(ParamsUtils.IMGURL.equals(key) || AROUTERURL.equals(key))
                    continue;
                if (stringBuffer.indexOf("?") == -1) {
                    stringBuffer.append("?");
                } else {
                    stringBuffer.append("&");
                }
                stringBuffer.append(key)
                        .append("=");
                Object value = datas.get(key);
                if(value instanceof Integer){
                    stringBuffer.append(datas.getInt(key));
                }else if(value instanceof Boolean){
                    stringBuffer.append(datas.getBoolean(key));
                }else{
                    stringBuffer.append(URLEncoder.encode(datas.getString(key), "UTF-8"));
                }
            }
            return stringBuffer.toString();
        }catch (Exception e) {
            return url;
        }

    }

    @Override
    public void postBindView(BaseCell cell) {
        super.postBindView(cell);
        if (tv_title != null) {
            tv_title.setText(getOptString(APPNAME));
        }
        loadImage(iv_img);
    }


    @Override
    public void postUnBindView(BaseCell cell) {
    }

    @Override
    protected void setCustomStyle() {
        setOptTextStyle(tv_title);
        setOptImageStyle(iv_img);
//        setImgLayoutParams();

    }

    protected void setImgLayoutParams() {
        int width = dpToPx(getOptInt(ParamsUtils.IMGWIDTH));
        int height = dpToPx(getOptInt(ParamsUtils.IMGHEIGHT));
        if (width == 0 && height != 0) {
            width = height;
        }
        if (height == 0 && width != 0) {
            height = width;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv_img.getLayoutParams();
        if (layoutParams != null) {
            if (width > 0 && height > 0) {
                layoutParams.width = width;
                layoutParams.height = height;
            }else{
                layoutParams.width = dpToPx(45);
                layoutParams.height = dpToPx(45);
            }
            iv_img.setLayoutParams(layoutParams);
        }
    }

}
