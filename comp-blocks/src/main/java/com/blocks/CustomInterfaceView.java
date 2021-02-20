package com.blocks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.image.progress.GlideApp;
import com.base.imagehelper.ImageHelper;
import com.blocks.R;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.view.ITangramViewLifeCycle;
import com.tmall.wireless.tangram.support.ExposureSupport;

import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * 使用接口方式的自定义View
 *
 * @author SunQiang
 * @since 2019-04-19
 */
public class CustomInterfaceView extends LinearLayout implements ITangramViewLifeCycle {
    private ImageView mImageView;
    private TextView mTextView;

    public CustomInterfaceView(Context context) {
        super(context);
        init();
    }

    public CustomInterfaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomInterfaceView(Context context, @Nullable AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int padding = Utils.dip2px(getContext(), 10);
        setPadding(padding, padding, padding, padding);
        mImageView = new ImageView(getContext());
        addView(mImageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mTextView = new TextView(getContext());
        mTextView.setPadding(0, padding, 0, 0);
        addView(mTextView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void cellInited(BaseCell cell) {
        setOnClickListener(cell);
        if (cell.serviceManager != null) {
            ExposureSupport exposureSupport = cell.serviceManager.getService(ExposureSupport.class);
            if (exposureSupport != null) {
                exposureSupport.onTrace(this, cell, cell.type);
            }
        }
    }

    @Override
    public void postBindView(BaseCell cell) {
        if (cell.pos % 2 == 0) {
            setBackgroundColor(0xffff0000);
            mImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            setBackgroundColor(0xff00ff00);
//            mImageView.setImageResource(R.mipmap.ic_launcher);
            GlideApp.with(getContext()).load(cell.optStringParam("imgUrl")).into(mImageView);
        }
        mTextView.setText(String.format(Locale.CHINA, "%s%d: %s", getClass().getSimpleName(),
                cell.pos, cell.optParam("text")));
    }

    @Override
    public void postUnBindView(BaseCell cell) {

    }
}
