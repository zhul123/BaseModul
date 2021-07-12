package com.blocks.views.floorviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.utils.ScreenUtils;
import com.blocks.views.R;
import com.tmall.ultraviewpager.UltraViewPager;

import androidx.viewpager.widget.ViewPager;


/**
 * @author :  zl
 * @date : 2020-10-09 11:31
 * @desc :底部下划线长度固定(使用PaTabLayout代码进行修改，主要修改不满屏时权重平分，超过屏幕后固定间距）因未验证是否影响其他功能，所以新建。
 */
public class TabBarFloorView extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    private Context mContext;
    private UltraViewPager mViewPager;

    private LinearLayout mTabsContainer;
    private int mTabCount;
    private int mCurrentTab;
    private float mCurrentPositionOffset;
    //指示器
    private float DEFAULT_INDICATORWIDTH = 32;
    private float DEFAULT_INDICATORHEIGHT = 1.5F;
    private float DEFAULT_INDICATORCORNERRADIUS = 100;
    private float DEFAULT_FIXED = 15;
    private float mIndicatorWidth;
    private float mIndicatorHeight;
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();
    private int mIndicatorColor = Color.parseColor("#FF6600");
    private float mIndicatorCornerRadius;
    //文字
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float DEFAULT_SELECTTEXTSIZE = 14;
    private String DEFAULT_TEXTSELECTCOLOR = "#FF6600";
    private String DEFAULT_TEXTUNSELECTCOLOR = "#6A6A6A";
    private float mSelectTextSize;
    private int mTextSelectColor;
    private int mTextUnSelectColor;
    //整个布局
    private int DEFAULT_HEIGHT_DP = 36;
    /**
     * 用于绘制显示器
     */
    private Rect mIndicatorRect = new Rect();
    /**
     * 用于实现滚动居中
     */
    private Rect mTabRect = new Rect();
    private float margin;

    public TabBarFloorView(Context context) {
        this(context, null);
    }

    public TabBarFloorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBarFloorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setFillViewport(true);//设置滚动视图是否可以伸缩其内容以填充窗口
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
        this.mContext = context;
        this.mTabsContainer = new LinearLayout(mContext);
        addView(mTabsContainer);
        obtainAttributes(context, attrs);
        String height = ViewGroup.LayoutParams.WRAP_CONTENT + "";
        if(attrs != null) {
             height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        }
        if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "") || height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.height = dp2px(DEFAULT_HEIGHT_DP);
            mTabsContainer.setLayoutParams(layoutParams);
        }
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.floor_TabBar);
//        这里面只需要设置，a.底部指示器的宽度  b.文字的颜色 c.文字的大小
        mIndicatorWidth = ta.getDimension(R.styleable.floor_TabBar_floor_indicator_width, dp2px(DEFAULT_INDICATORWIDTH));
        mIndicatorHeight = ta.getDimension(R.styleable.floor_TabBar_floor_indicator_height, dp2px(DEFAULT_INDICATORHEIGHT));
        mIndicatorCornerRadius = ta.getDimension(R.styleable.floor_TabBar_floor_indicator_radius, dp2px(DEFAULT_INDICATORCORNERRADIUS));
        //文字
        mSelectTextSize = ta.getDimension(R.styleable.floor_TabBar_floor_indicator_textSelectSize, sp2px(DEFAULT_SELECTTEXTSIZE));
        mTextSelectColor = ta.getColor(R.styleable.floor_TabBar_floor_indicator_textSelectColor, Color.parseColor(DEFAULT_TEXTSELECTCOLOR));
        mTextUnSelectColor = ta.getColor(R.styleable.floor_TabBar_floor_indicator_textUnSelectColor, Color.parseColor(DEFAULT_TEXTUNSELECTCOLOR));

        ta.recycle();
    }

    public void setViewPager(UltraViewPager vp) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }
        this.mViewPager = vp;
        mViewPager.setOnPageChangeListener(this);
        notifyDataSetChanged();

    }

    private void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mViewPager.getAdapter().getCount();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = View.inflate(mContext, R.layout.floor_layout_tab, null);
            tabView.setPadding(dp2px(DEFAULT_FIXED),0,dp2px(DEFAULT_FIXED),0);
            CharSequence pageTitle = mViewPager.getAdapter().getPageTitle(i);
            addTab(i, pageTitle == null ? "" :pageTitle.toString(), tabView, isFixedWidth(mTabCount));
        }
        updateTabStyles();
    }

    /**
     * 是否为固定间距（如果tab超过屏幕宽度，则为固定间距，否则为权重显示）
     */
    private boolean isFixedWidth(int mTabCount){
        if(mTabCount <= 1){
            return true;
        }
        try {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < mTabCount; i++) {
                stringBuffer.append(mViewPager.getAdapter().getPageTitle(i));
            }
            //所有字体宽度
            String str = stringBuffer.toString();
            float allTextWidth = mTextPaint.measureText(str);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.getLayoutParams();
            //tab宽度（字体宽度+间距宽度）
            float tabWidth = allTextWidth + 2 * mTabCount * dp2px(DEFAULT_FIXED)
                    + this.getPaddingLeft()
                    + this.getPaddingRight();
//            如果tab按照固定间距计算宽度 > 屏幕宽度  则为固定宽度，反之为权重填充
            return tabWidth > ScreenUtils.getScreenBounds(mContext)[0];
        }catch (Exception e){
            return true;
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            View view = mTabsContainer.getChildAt(i);
            TextView tv_tab_title = view.findViewById(R.id.tv_tab_title);
            if (tv_tab_title != null) {
                tv_tab_title.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnSelectColor);
                tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSelectTextSize);
            }
        }
    }

    private void addTab(final int position, String title, View tabView, boolean isFixed) {
        if(tabView == null)
            return;
        TextView tv_tab_title = tabView.findViewById(R.id.tv_tab_title);
        if (tv_tab_title != null && title != null) {
            tv_tab_title.setText(title);
            tv_tab_title.setPadding(0,0,0,0);
        }
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mTabsContainer.indexOfChild(v);
                if (position != -1) {
                    if (mViewPager.getCurrentItem() != position) {
                        mViewPager.setCurrentItem(position);
                    }
                    if (mListener != null) {
                        mListener.onTabSelect(position);
                    }
                } else {
                    if (mListener != null) {
                        mListener.onTabSelect(position);
                    }
                }
            }
        });
        //将屏宽均分
        LinearLayout.LayoutParams lp_tab = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if(!isFixed){
            lp_tab.width = 0;
            lp_tab.weight = 1;
            tabView.setPadding(0,0,0,0);
        }
        mTabsContainer.addView(tabView, position,lp_tab);

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    private int mLastScrollX;

    private void scrollToCurrentTab() {
        if (mTabCount <= 0) return;
        int offset = (int) (mCurrentPositionOffset * mTabsContainer.getChildAt(mCurrentTab).getWidth());
        int newScrollX = mTabsContainer.getChildAt(mCurrentTab).getLeft() + offset;
        if (mCurrentTab > 0 || offset > 0) {
            newScrollX -= getWidth() / 2 - getPaddingLeft();
            calcIndicatorRect();
            newScrollX += ((mTabRect.right - mTabRect.left) / 2);
        }
        if (newScrollX != mLastScrollX) {
            mLastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }


    private void calcIndicatorRect() {
        View currentTabView = mTabsContainer.getChildAt(mCurrentTab);
        float left = currentTabView.getLeft();
        float right = currentTabView.getRight();

        TextView tab_title = currentTabView.findViewById(R.id.tv_tab_title);
        mTextPaint.setTextSize(mSelectTextSize);
        float textWidth = mTextPaint.measureText(tab_title.getText().toString());
        margin = (right - left - textWidth) / 2;
        if (mCurrentTab < mTabCount - 1) {
            View nextTabView = mTabsContainer.getChildAt(this.mCurrentTab + 1);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();
            left += mCurrentPositionOffset * (nextTabLeft - left);
            right += mCurrentPositionOffset * (nextTabRight - right);
            TextView next_tab_title = nextTabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(mSelectTextSize);
            float nextTextWidth = mTextPaint.measureText(next_tab_title.getText().toString());
            float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2;
            margin = margin + mCurrentPositionOffset * (nextMargin - margin);
        }
        mIndicatorRect.left = (int) left;
        mIndicatorRect.right = (int) right;
        mIndicatorRect.left = (int) (left + margin - 1);
        mIndicatorRect.right = (int) (right - margin - 1);
        mTabRect.left = (int) left;
        mTabRect.right = (int) right;

        float indicatorLeft = currentTabView.getLeft() + (currentTabView.getWidth() - mIndicatorWidth) / 2;
        if (this.mCurrentTab < mTabCount - 1) {
            View nextTab = mTabsContainer.getChildAt(this.mCurrentTab + 1);
            indicatorLeft = indicatorLeft + mCurrentPositionOffset * (currentTabView.getWidth() / 2 + nextTab.getWidth() / 2);
        }

        mIndicatorRect.left = (int) indicatorLeft;
        mIndicatorRect.right = (int) (mIndicatorRect.left + mIndicatorWidth);


    }

    @Override
    public void onPageSelected(int position) {
        updateTabSelection(position);

    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; i++) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = tabView.findViewById(R.id.tv_tab_title);
            if (tab_title != null) {
                tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnSelectColor);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || mTabCount <= 0) {
            return;
        }
        int height = getHeight();
        calcIndicatorRect();

        if (mIndicatorHeight > 0) {
            mIndicatorDrawable.setColor(mIndicatorColor);
            mIndicatorDrawable.setBounds(mIndicatorRect.left,
                    height - (int) mIndicatorHeight,
                    mIndicatorRect.right,
                    height);
            mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
            mIndicatorDrawable.draw(canvas);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
                scrollToCurrentTab();
            }
        }
        super.onRestoreInstanceState(state);
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    public interface OnTabSelectListener {
        void onTabSelect(int position);

        void onTabReselect(int position);

    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
