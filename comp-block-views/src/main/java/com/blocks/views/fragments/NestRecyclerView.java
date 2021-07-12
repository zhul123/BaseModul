package com.blocks.views.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.base.utils.ScreenUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NestRecyclerView extends RecyclerView {
    private static final float DEFAULTFIX = 10;//滑动事件处理最小值（即超过此距离进行滑动事件处理）
    private int lastVisibleItemPosition;
    private int firstVisibleItemPosition;
    private float mX1 = 0;// 记录downX位置
    private float mY1 = 0;// 记录downY位置
    private boolean isTopToBottom = false;//到顶了
    private boolean isBottomToTop = false;//到底了
    private int viewHeight;
    private Rect rect = new Rect();

    public NestRecyclerView(@NonNull Context context) {
        super(context);
    }

    public NestRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
//        Log.d("nestScrolling", "onInterceptTouchEvent:");
        //如果控件全部显示则拦截点击事件
//        if(isGlobalVisible()) {
        onTouchEvent(e);
//        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d("nestScrolling", "dispatchTouchEvent:");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mY1 = event.getY();
                mX1 = event.getX();
                //不允许父View拦截事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isGlobalVisible()){
//        Log.d("nestScrolling", "isGlobalVisible:");
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }

                //如果x滑动大，则为左右滑动
                if(Math.abs(event.getX() - mX1) > Math.abs(event.getY() - mY1)){
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }

               float nowY = event.getY();
                isIntercept(nowY);
                if (hasOverlappingRendering()) {
                    if (isBottomToTop || isTopToBottom) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                break;
            case MotionEvent.ACTION_SCROLL:

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mX1 = mY1 = 0;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * item是否完全显示
     *
     * @return
     */
    private boolean isGlobalVisible() {
        viewHeight = getHeight();
        getGlobalVisibleRect(rect);
//        Log.d("nestScrolling", "isGlobalVisible:");
        //如果控件全部显示则拦截点击事件
        if (rect.bottom - rect.top == viewHeight) {
            return true;
        }
        return false;
    }

    /**
     * 是否置顶
     *
     * @return
     */

    private void isIntercept(float nowY) {

//        Log.d("nestScrolling", "nowY:"+nowY);
        isTopToBottom = false;
        isBottomToTop = false;

        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            //得到当前界面，最后一个子视图对应的position
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            //得到当前界面，第一个子视图的position
            firstVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            //得到当前界面，最后一个子视图对应的position
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            //得到当前界面，第一个子视图的position
            firstVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();
        }
        //得到当前界面可见数据的大小
        int visibleItemCount = layoutManager.getChildCount();
        //得到RecyclerView对应所有数据的大小
        int totalItemCount = layoutManager.getItemCount();
        if (visibleItemCount > 0) {
            if (lastVisibleItemPosition == totalItemCount - 1) {
                //最后视图对应的position等于总数-1时，说明上一次滑动结束时，触底了

                /**
                 * 注意这里有非常关键的两点，也是我修改完善之前哥们博客的有坑的两点，
                 * 第一点是canScrollVertically传的正负值问题，判断向上用正值1，向下则反过来用负值-1，
                 * 第二点是canScrollVertically返回值的问题，true时是代表可以滑动，false时才代表划到顶部或者底部不可以再滑动了，所以这个判断前要加逻辑非!运算符
                 * 补充了这两点基本效果就很完美了。
                 */
                if (!NestRecyclerView.this.canScrollVertically(1) && nowY < mY1) {
                    // 不能向上滑动
//                    Log.d("nestScrolling", "不能向上滑动");
                    isBottomToTop = true;
                } else {
//                    Log.d("nestScrolling", "向下滑动");
                }
            } else if (firstVisibleItemPosition == 0) {
                //第一个视图的position等于0，说明上一次滑动结束时，触顶了
//                Log.d("nestScrolling", "触顶了");
                if (!NestRecyclerView.this.canScrollVertically(-1) && nowY > mY1) {
                    // 不能向下滑动
                    Log.d("nestScrolling", "不能向下滑动");
                    isTopToBottom = true;
                } else {
//                    Log.d("nestScrolling", "向上滑动");
                }
            }
        }
    }
}
