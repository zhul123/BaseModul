package com.base.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


import com.base.utils.ScreenUtils;

import androidx.annotation.Nullable;

public class WaterMarkView extends View {
    static final int POINTFIX = ScreenUtils.dipToPx(10);
    static final int MARGIN = ScreenUtils.dipToPx(30);
    static final int PAINTWIDTH = ScreenUtils.dipToPx(3);
    static final int TEXTSIZE = ScreenUtils.dipToPx(14);
    int[][] num0 = {{0,1},{1,1}};
    int[][] num1 = {{1,0},{0,0}};
    int[][] num2 = {{1,0},{1,0}};
    int[][] num3 = {{1,1},{0,0}};
    int[][] num4 = {{1,1},{0,1}};
    int[][] num5 = {{1,0},{0,1}};
    int[][] num6 = {{1,1},{1,0}};
    int[][] num7 = {{1,1},{1,1}};
    int[][] num8 = {{1,0},{1,1}};
    int[][] num9 = {{0,1},{1,0}};
    public enum MarkType{
        POINT,TEXT
    }
    Paint mPaint;
    private MarkType mMarkType;
    private String mDrawText;
    public WaterMarkView(Context context) {
        super(context);
        init();
    }

    public WaterMarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterMarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WaterMarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#000000"));
        mPaint.setAlpha((int) (255 * 0.2));
        mPaint.setStyle(Paint.Style.FILL);
        //设置画笔宽度为30px
        mPaint.setStrokeWidth(PAINTWIDTH);
        mPaint.setTextSize(TEXTSIZE);
    }

    /**
     * 生成水印类型
     * @param markType markType 为 MarkType.POINT 时text必须为数字串，否则无法生成水印
     * @param text
     */
    public void setDraw(MarkType markType, String text){
        this.mDrawText = text;
        this.mMarkType = markType;
        invalidate();//重绘（不会只想onMease和onLayout)
    }

    private int screenWidth;
    private int screenHeight;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMarkBitmap(canvas,mDrawText);
    }

    public void drawMarkBitmap(Canvas canvas, String gText) {
        int sideLength;

        if (screenHeight == 0) {
            screenHeight = ScreenUtils.getScreenBounds(getContext())[1];
        }
        if (screenWidth == 0) {
            screenWidth = ScreenUtils.getScreenBounds(getContext())[0];
        }

        if (screenWidth > screenHeight) {
            sideLength = (int) Math.sqrt(2 * (screenWidth * screenWidth));

        } else {
            sideLength = (int) Math.sqrt(2 * (screenHeight * screenHeight));

        }
        Rect rect = new Rect();
//获取文字长度和宽度
        int strwid = 0;
        int strhei = 0;
        Bitmap pointBitMap = null;
        if(mMarkType == MarkType.TEXT) {
            mPaint.getTextBounds(gText, 0, gText.length(), rect);
            strwid = rect.width();
            strhei = rect.height();
        }else if(mMarkType == MarkType.POINT){
            pointBitMap = createBitMap(gText);
            strwid = oneWidth;
            strhei = oneHeight;
        }
//先平移，再旋转才不会有空白，使整个图片充满
        if (screenWidth > screenHeight) {
            canvas.translate(screenWidth - sideLength - MARGIN, sideLength - screenWidth + MARGIN);
        } else {
            canvas.translate(screenHeight - sideLength - MARGIN, sideLength - screenHeight + MARGIN);
        }
        //将该文字图片逆时针方向倾斜45度
        canvas.rotate(-45);
        for (int i = 0; i <= sideLength; ) {
            int count = 0;
            for (int j = 0; j <= sideLength; count++) {
                if (count % 2 == 0) {
                    if(mMarkType == MarkType.POINT) {
                        canvas.drawBitmap(pointBitMap, i, j, mPaint);
                    }else if(mMarkType == MarkType.TEXT){
                        canvas.drawText(gText, i, j, mPaint);
                    }
                } else {
                    //偶数行进行错开
                    if(mMarkType == MarkType.POINT) {
                        canvas.drawBitmap(pointBitMap, i + strwid / 2, j, mPaint);
                    }else if(mMarkType == MarkType.TEXT){
                        canvas.drawText(gText, i + strwid / 2, j, mPaint);
                    }
                }
                j = (int) (j + MARGIN + strhei);
            }
            i = (int) (i + strwid + MARGIN);
        }
        canvas.save();
        if(pointBitMap!= null){
            pointBitMap.recycle();
        }
    }

    int oneWidth = 0;
    int oneHeight = 0;
    private Bitmap createBitMap(String numStr){
        if(oneHeight == 0 || oneWidth == 0){
            oneWidth = (numStr.length()-1) * (POINTFIX + PAINTWIDTH) * 2;
            oneHeight = 2 * (POINTFIX + PAINTWIDTH);
        }
        Bitmap bitmap = Bitmap.createBitmap(oneWidth, oneHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        for(int k = 0;k<numStr.length();k++){
            int[][] temp = getNumInt(numStr.charAt(k)+"");
            if(temp == null){
                continue;
            }
            for(int i=0;i<2;i++){
                for(int j=0;j<2;j++){
                    if(temp[i][j] == 1){
                        float x = (i+1+2*k) * POINTFIX;
                        float y = (j+1) * POINTFIX;
                        canvas.drawPoint(x,y,mPaint);
                    }
                }
            }
        }
        canvas.drawBitmap(bitmap, new Matrix(), mPaint);
        canvas.save();
        return bitmap;
    }
    private int[][] getNumInt(String numStr){
        System.out.println("=======num:"+numStr);
        switch (numStr){
            case "0":
                return num0;
            case "1":
                return num1;
            case "2":
                return num2;
            case "3":
                return num3;
            case "4":
                return num4;
            case "5":
                return num5;
            case "6":
                return num6;
            case "7":
                return num7;
            case "8":
                return num8;
            case "9":
                return num9;
        }
        return null;
    }
}
