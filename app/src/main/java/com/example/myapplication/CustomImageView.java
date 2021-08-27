package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created by dushu on 2021/3/31 .
 */
public class CustomImageView extends AppCompatImageView {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float _borderRadius = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
