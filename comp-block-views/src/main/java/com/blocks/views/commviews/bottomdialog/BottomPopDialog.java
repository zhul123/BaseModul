package com.blocks.views.commviews.bottomdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.base.utils.ScreenUtils;
import com.blocks.views.R;
import com.blocks.views.floorviews.TextFloorView;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.support.SimpleClickSupport;

import org.json.JSONArray;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: wcd
 * @email: wcdwangyi@163.com
 * @date: 2020/1/15 2:42 PM
 * @desc:
 */
public class BottomPopDialog extends Dialog {
    private RecyclerView recyclerView;
    private TangramEngine engine;
    private DialogBuilder dialogBuilder;
    private Context mContext;
    private View topbody;
    private TextView tvCancel;
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
        recyclerView = findViewById(R.id.recyclerView);
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
            builder.registerCell("text", TextFloorView.class);
            engine = builder.build();
            engine.addSimpleClickSupport(dialogBuilder.mSimpleClickSupport);
            engine.bindView(recyclerView);
            engine.setData(dialogBuilder.list);

            //最大高度设置--最多显示9个半的条目
            if(recyclerView.getAdapter().getItemCount() > ITEM_MAX_SHOW) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topbody.getLayoutParams();
                params.height = ScreenUtils.getScreenBounds(getContext())[1] / 3;
                topbody.setLayoutParams(params);
            }

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

    public void setDatas(JSONArray jsonArray){
        if(jsonArray != null && engine != null) {
            engine.setData(jsonArray);
            engine.refresh();
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
