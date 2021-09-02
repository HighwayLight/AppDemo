package com.example.myapplication.PhotoAlbum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by dushu on 2021/9/1 .
 */
public class SelectedDrawable extends Drawable {

    private Paint mPaint;
    private int radius = 20;
    private int num = 1;
    private int textSize = 10;

    public SelectedDrawable(int radius, int num, int textSize) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.radius = radius;
        this.num = num;
        this.textSize = textSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        Rect r = getBounds();
        RectF rect = new RectF(0, 0, r.width(), r.height());
        int sc = canvas.saveLayer(rect, null);

        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#FF4EC899"));
        canvas.drawCircle(radius, radius, radius, mPaint); //绘制圆形
        mPaint.reset();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        //计算baseline
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = rect.centerY() + distance;
        canvas.drawText( String.valueOf(num), rect.centerX(), baseline, mPaint);

        canvas.restoreToCount(sc);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    //还需要从重写以下2个方法，返回drawable实际宽高
    @Override
    public int getIntrinsicWidth() {
        return radius * 2;
    }

    @Override
    public int getIntrinsicHeight() {
        return radius * 2;
    }

}
