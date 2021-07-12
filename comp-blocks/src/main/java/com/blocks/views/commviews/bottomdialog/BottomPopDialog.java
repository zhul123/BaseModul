package com.blocks.views.commviews.bottomdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.base.http.MyHttpUtils;
import com.base.utils.JsonUtils;
import com.base.utils.ScreenUtils;
import com.base.utils.ToastUtil;
import com.blocks.R;
import com.lib.block.style.Params;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.support.SimpleClickSupport;

import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @desc:选择框
 */
public class BottomPopDialog extends Dialog {
    private RecyclerView recyclerView;
    private TangramEngine engine;
    private DialogBuilder dialogBuilder;
    private Context mContext;
    private View topbody;
    private View mProgressBar;
    private TextView tvCancel;
    private EditText mEditText;
    private static final int ITEM_HEIGHT = 44;
    private static final int ITEM_MAX_SHOW = 10;
    private static final int BG_RADIU = 4;

    public BottomPopDialog(@NonNull Context context) {
        this(context, 0);
    }

    public BottomPopDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.commonDialog);
        this.mContext = context;

    }

    public static DialogBuilder newDialogBuilder(Context activity) {
        return new DialogBuilder(activity);
    }

    public BottomPopDialog(DialogBuilder dialogBuilder) {
        this(dialogBuilder.activity);
        this.dialogBuilder = dialogBuilder;
        this.mContext = dialogBuilder.activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bottom_pop);
        topbody = findViewById(R.id.topbody);
        mProgressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recyclerView);
        mEditText = findViewById(R.id.et_search);
        tvCancel = findViewById(R.id.tvCancel);

        if (dialogBuilder != null) {
            //点击屏幕或物理返回键，dialog不消失
            if (dialogBuilder.cancelable != null) {
                setCancelable(dialogBuilder.cancelable);
            }
            //点击屏幕，dialog不消失；点击物理返回键dialog消失
            if (dialogBuilder.canceledOnTouchOutside != null) {
                setCanceledOnTouchOutside(dialogBuilder.canceledOnTouchOutside);
            }

            TangramBuilder.InnerBuilder builder = TangramBuilder.newInnerBuilder(mContext);
            try {
                Class cls = Class.forName("com.blocks.views.floorviews.TextFloorView");
                builder.registerCell("textView", cls);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
//            builder.registerCell("text", TextFloorView.class);
            engine = builder.build();
            engine.addSimpleClickSupport(dialogBuilder.mSimpleClickSupport);
            engine.bindView(recyclerView);
            if(dialogBuilder.list != null) {
                setDatas(dialogBuilder.list,false);
            }else{
                loadData(dialogBuilder.dataUrl);
            }
            setTopLayoutParam();

            //取消按钮
            String cancelStr = dialogBuilder.cancelStr;
            if (TextUtils.isEmpty(cancelStr)) {
                cancelStr = "取消";
            }
            tvCancel.setText(cancelStr);
            int cancelTextColor = dialogBuilder.cancelColor;
            if (cancelTextColor == 0) {
                cancelTextColor = Color.parseColor("#FF6600");
            }
            tvCancel.setTextColor(cancelTextColor);

            if (dialogBuilder.cancelIsBold) {
                tvCancel.getPaint().setFakeBoldText(true);
            }

            topbody.setBackgroundDrawable(getGradientDrawable(dialogBuilder.contentBgColor, ScreenUtils.dipToPx(getContext(),dialogBuilder.contentBgRadius)));
            tvCancel.setBackgroundDrawable(getGradientDrawable(dialogBuilder.cancelBgColor, ScreenUtils.dipToPx(getContext(),dialogBuilder.cancelBgRadius)));

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                    if (dialogBuilder.runnable != null) {
                        dialogBuilder.runnable.cancleBack();
                    }
                }
            });


        }
    }

    private void setTopLayoutParam(){
        //最大高度设置--最多显示9个半的条目
        if(recyclerView.getAdapter().getItemCount() > ITEM_MAX_SHOW) {
            mEditText.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topbody.getLayoutParams();
            params.height = ScreenUtils.getScreenBounds(getContext())[1] / 5 * 2;
            topbody.setLayoutParams(params);
            mEditText.addTextChangedListener(mTextWatcher);
        }else {
            mEditText.setVisibility(View.GONE);
            mEditText.removeTextChangedListener(mTextWatcher);
        }
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s == null || TextUtils.isEmpty(s.toString())){
                setDatas(dialogBuilder.list,true);
            }
            Iterator iterator = dialogBuilder.list.iterator();
            while (iterator.hasNext()){
                JSONObject jsonObject = (JSONObject) iterator.next();
                String str = "";
                try {
                    str = jsonObject.getString(Params.TEXT);
                    if(TextUtils.isEmpty(str)) {
                        str = jsonObject.getJSONObject(Params.DATAS).getString(Params.TEXT);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(str.indexOf(s.toString()) != -1){
                    dialogBuilder.searchResultList.add(jsonObject);
                }
            }
            setDatas(dialogBuilder.searchResultList,true);
        }
    };

    private void loadData(String dataUrl){
        mProgressBar.setVisibility(View.VISIBLE);
        if(dataUrl != null){
            MyHttpUtils.get(dataUrl, null, new MyHttpUtils.HttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    JSONArray jsonArray = ChooseDialogTransform.transform(result);
                    if(engine != null){
                        setDatas(jsonArray,false);
                        setTopLayoutParam();
                    }
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFail(String errMsg) {
                    ToastUtil.getInstance().makeText(errMsg);
                }
            },this);
        }
    }

    public void setDatas(JSONArray jsonArray,boolean isSearch){
        if(jsonArray != null && engine != null) {
            if(!isSearch) {
                dialogBuilder.list = jsonArray;
            }
            try {
                engine.setData(JsonUtils.getInstance().transFastJsonArrayToJsonArray(jsonArray));
                engine.refresh();
                dialogBuilder.searchResultList.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setDatas(String datas){
        if(datas != null && engine != null) {
            JSONArray jsonArray = null;
            try {
                jsonArray = JSON.parseArray(datas);
            }catch (Exception e){

            }
            if(jsonArray != null) {
                setDatas(jsonArray,false);
            }else{
                loadData(datas);
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void onDestroy(){
        if (engine != null){
            engine.destroy();
        }
    }

    @Override
    public void show() {
        super.show();
        try {
            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            //动画
            window.setWindowAnimations(R.style.dialog_anim_style);
            //宽度
            int[] windowScreen = ScreenUtils.getScreenBounds(getContext());
            lp.width = (int) (windowScreen[0] * 330F / 360);
            //底部弹出
            window.setGravity(Gravity.BOTTOM);

            window.setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected GradientDrawable getGradientDrawable(int bgColor, int cornersRadius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(bgColor);
        gradientDrawable.setCornerRadii(new float[]{cornersRadius, cornersRadius, cornersRadius, cornersRadius, cornersRadius, cornersRadius, cornersRadius, cornersRadius});
        return gradientDrawable;
    }


    /**
     * 不支持：动画开启、文字大小
     * 支持：
     * （1）列表文字：自定义文字一般颜色、自定义文字高亮颜色、自定义文字高亮的时候是否加粗
     * （2）取消按钮文字：内容、颜色
     * （2）上面列表的背景：颜色、圆角，默认为白色、4dip
     * （3）下面取消的背景：颜色、圆角，默认为白色、4dip
     * （4）是否可以触摸其他地方取消，是否拦截返回取消
     */
    public static class DialogBuilder {

        private Context activity;
        private SimpleClickSupport mSimpleClickSupport;
        //列表
        private JSONArray list;
        private JSONArray searchResultList = new JSONArray();
        private String dataUrl;

        //取消按钮
        private String cancelStr;//默认为取消
        private int cancelColor;
        private boolean cancelIsBold;
        private int cancelBgColor = Color.WHITE;
        private int cancelBgRadius = 8;
        private int contentBgColor = Color.WHITE;
        private int contentBgRadius = 8;
        //是否
        private Boolean cancelable = true;
        private Boolean canceledOnTouchOutside;
        //回调
        private OnClickListener runnable;

        public DialogBuilder(Context activity) {
            this.activity = activity;
        }

        public SimpleClickSupport getSimpleClickSupport() {
            return mSimpleClickSupport;
        }

        public DialogBuilder setSimpleClickSupport(SimpleClickSupport simpleClickSupport) {
            mSimpleClickSupport = simpleClickSupport;
            return this;
        }

        public DialogBuilder setContent(JSONArray jsonArray) {
            this.list = jsonArray;
            return this;
        }

        public DialogBuilder setContent(String chooseDatas) {
            try {
                this.list = JSON.parseArray(chooseDatas);
                if (list == null) {
                    dataUrl = chooseDatas;
                }
            }catch (Exception e) {
                dataUrl = chooseDatas;
            }
            return this;
        }

        //取消按钮

        public DialogBuilder setCancelStr(String cancelStr) {
            this.cancelStr = cancelStr;
            return this;
        }

        public DialogBuilder setCancelStr(int cancelColor) {
            this.cancelColor = cancelColor;
            return this;
        }

        public DialogBuilder setCancelIsBold(boolean cancelIsBold) {
            this.cancelIsBold = cancelIsBold;
            return this;
        }

        public DialogBuilder setCancelIsBold(int cancelBgColor) {
            this.cancelBgColor = cancelBgColor;
            return this;
        }

        public DialogBuilder setCancelBgRadius(int cancelBgRadius) {
            this.cancelBgRadius = cancelBgRadius;
            return this;
        }


        //消失
        public DialogBuilder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public DialogBuilder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }


        //回调
        public DialogBuilder setClickListener(OnClickListener runnable) {
            this.runnable = runnable;
            return this;
        }

        public BottomPopDialog build() {
            return new BottomPopDialog(this);
        }


    }


    public interface OnClickListener {

        void cancleBack();
    }
}
