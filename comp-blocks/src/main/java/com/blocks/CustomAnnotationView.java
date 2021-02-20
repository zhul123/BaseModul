package com.blocks;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blocks.R;
import com.tmall.wireless.tangram.structure.BaseCell;
import com.tmall.wireless.tangram.structure.CellRender;
import com.tmall.wireless.tangram.support.ExposureSupport;

import java.util.Locale;

import androidx.annotation.Nullable;

/**
 * 使用注解方式的自定义View
 *
 * @author SunQiang
 * @since 2019-04-19
 */
public class CustomAnnotationView extends LinearLayout {
    private ImageView mImageView;
    private TextView mTextView;

    public CustomAnnotationView(Context context) {
        super(context);
        init();
    }

    public CustomAnnotationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomAnnotationView(Context context, @Nullable AttributeSet attrs,
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

    @CellRender
    public void cellInited(BaseCell cell) {
        setOnClickListener(cell);
        if (cell.serviceManager != null) {
            ExposureSupport exposureSupport = cell.serviceManager.getService(ExposureSupport.class);
            if (exposureSupport != null) {
                exposureSupport.onTrace(this, cell, cell.type);
            }
        }
    }

    @CellRender
    public void postBindView(final BaseCell cell) {
        if (cell.pos % 2 == 0) {
            setBackgroundColor(0xff0000ff);
            mImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
            setBackgroundColor(0xff00ffff);
            mImageView.setImageResource(R.mipmap.ic_launcher);
        }
        mTextView.setText(String.format(Locale.CHINA, "%s%d: %s", getClass().getSimpleName(),
                cell.pos, cell.optParam("text")));
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(),
//                        "wo dianjile ，type=" + cell.stringType + ", pos=" + cell.pos,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @CellRender
    public void postUnBindView(BaseCell cell) {

    }
}
