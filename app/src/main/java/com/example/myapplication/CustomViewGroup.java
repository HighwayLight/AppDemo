package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by dushu on 2021/3/30 .
 */
public class CustomViewGroup extends FrameLayout {


    private final RectF roundRect = new RectF();
    private final Paint desPaint = new Paint();
    private final Paint srcPaint = new Paint();

    private final Paint pathPaint = new Paint();

    private float mRadius = 50;
    private boolean isChange = false;

    private int strokeWidth;    // 边框线宽
    private int strokeColor;    // 边框颜色

    protected final Path _path = new Path();

    public CustomViewGroup(@NonNull Context context) {
        this(context, null);
    }

    public CustomViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        desPaint.setAntiAlias(true);//设置抗锯齿
        desPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        srcPaint.setAntiAlias(true);
        pathPaint.setAntiAlias(true);
        float density = getResources().getDisplayMetrics().density;
        mRadius *= density;
    }

    public void change(boolean isChange) {
        this.isChange = isChange;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        roundRect.set(0, 0, width, height);
    }

    @Override
    public void draw(Canvas canvas) {

        //保存最原始的roundRect
        canvas.saveLayer(roundRect, srcPaint, Canvas.ALL_SAVE_FLAG);
        if (isChange) {
            //保存去掉头部2圆角的roundRect(实际就是保留底部的2个圆角)
            canvas.drawRect(roundRect.left, (roundRect.top + roundRect.bottom) / 2, roundRect.right, roundRect.bottom, srcPaint);
            //保存去掉底部2圆角的roundRect(实际就是保留顶部的2个圆角)
//   canvas.drawRect(roundRect.left, roundRect.top, roundRect.right, roundRect.bottom / 2, srcPaint);
        }
        //保存掉头部2圆角的roundRect
        canvas.drawRoundRect(roundRect, mRadius, mRadius, srcPaint);
        //保存叠加后的内容
        canvas.saveLayer(roundRect, desPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        //清空所有的图像矩阵修改状态
//        canvas.restore();

//        _path.reset();
//        _path.addRoundRect(roundRect,mRadius,mRadius,Path.Direction.CW);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(10);
        pathPaint.setColor(Color.BLUE);
        pathPaint.setAlpha(0x0ff & (Color.BLUE >> 24));
//        canvas.drawPath(_path,pathPaint);
        canvas.drawRoundRect(roundRect, mRadius, mRadius, pathPaint);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
