package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by dushu on 2021/3/30 .
 */
public class CustomViewGroup02 extends FrameLayout {


    protected final Paint _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    private float _borderRadius = 10;

    private int _borderWidth = 2;    // 边框线宽
    private int _borderColor = Color.GREEN;    // 边框颜色

    public CustomViewGroup02(@NonNull Context context) {
        this(context, null);
    }

    public CustomViewGroup02(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewGroup02(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        _borderRadius *= density;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        rect.set(0, 0, width, height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect.width() <= 0 || rect.height() <= 0) {
            return;
        }
        canvas.saveLayer(rect, null);
        if ( _borderRadius > 0) {
            _paint.reset();
            _paint.setColor(Color.WHITE);
            _paint.setAntiAlias(true);
            _paint.setDither(true);

            Drawable drawable = getResources().getDrawable(R.mipmap.car);
            Bitmap bitmap = drawableToBitmap(drawable, (int) rect.width(), (int) rect.height());
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            _paint.setShader(bitmapShader);

            canvas.drawRoundRect(rect, _borderRadius, _borderRadius, _paint);
        }

        if (_borderColor != 0 && _borderWidth > 0) {
            _paint.reset();
            _paint.setColor(_borderColor);
            _paint.setAlpha(0x0ff & (_borderColor >> 24));
            _paint.setStyle(Paint.Style.STROKE);
            _paint.setStrokeWidth(_borderWidth);
            _paint.setAntiAlias(true);

            canvas.drawRoundRect(rect, _borderRadius, _borderRadius, _paint);

        }

        canvas.restore();

    }

    private Bitmap drawableToBitmap(Drawable drawable, int w, int h) {

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
