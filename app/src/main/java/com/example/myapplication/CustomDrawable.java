package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by dushu on 2021/1/26 .
 */
public class CustomDrawable extends Drawable {

    protected final Paint _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Path _path = new Path();

    protected Drawable _backgroundImage = null;
    protected int _borderRadius = 0;

    protected int w = 0;
    protected int h = 0;

    public CustomDrawable(Drawable _backgroundImage) {
        this._backgroundImage = _backgroundImage;
        this._borderRadius = 40;
    }

    public CustomDrawable(int w, int h) {
        this.w = w;
        this.h = h;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(@NonNull Canvas canvas) {


        Rect r = getBounds();
        RectF rect = new RectF(0, 0, r.width(), r.height());
        int sc = canvas.saveLayer(rect, null);
        if (_backgroundImage != null) {
//            _backgroundImage.setBounds(r);
//            _backgroundImage.setAlpha(getAlpha());
//            _backgroundImage.draw(canvas);

            if (_borderRadius > 0) {

//                Path path = new Path();
//
//                path.addRoundRect(rect, _borderRadius,_borderRadius, Path.Direction.CW);
//
//                _paint.reset();
//                _paint.setColor(Color.RED);
//                _paint.setStyle(Paint.Style.FILL);
//
//                _paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//
//                canvas.drawPath(path,_paint);
//
//                _paint.setXfermode(null);
                Log.e("TAG", " draw()");
                Bitmap bitmap = drawableToBitmap(_backgroundImage, (int) rect.width(), (int) rect.height());

                BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                _paint.reset();
                _paint.setShader(bitmapShader);

                canvas.drawRoundRect(rect, _borderRadius, _borderRadius, _paint);
            }
        } else {
            _paint.reset();
            int[] colors = {Color.RED, Color.GREEN, Color.BLUE};
            float[] position = {0f, 0.5f, 1.0f};
            int x = getIntrinsicWidth();
            if (x == -1) {
                x = w;
            }
            int y = getIntrinsicHeight();
            if (y == -1) {
                y = h;
            }
            LinearGradient linearGradient = new LinearGradient(rect.width(), 0, 0, 0, colors, position, Shader.TileMode.CLAMP);

            _paint.setShader(linearGradient);
            canvas.drawRect(0, 0, x, y, _paint);


        }

        canvas.restoreToCount(sc);
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

    /**
     * getIntrinsicWidth、getIntrinsicHeight主要是为了在View使用wrap_content的时候，
     * 提供一下尺寸，默认为-1可不是我们希望的
     */
    @Override
    public int getIntrinsicHeight() {
        return _backgroundImage != null ? _backgroundImage.getIntrinsicHeight() : -1;
    }

    @Override
    public int getIntrinsicWidth() {
        return _backgroundImage != null ? _backgroundImage.getIntrinsicWidth() : -1;
    }

    @Override
    public void setAlpha(int alpha) {
        _paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        _paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
