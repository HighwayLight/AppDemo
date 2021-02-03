package com.example.myapplication.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myapplication.R;

/**
 * Create by zhongyao on 2021/2/3
 * Description:相加聚焦动画框
 */
public class CameraFocusBar extends AppCompatImageView {

    public CameraFocusBar(Context context) {
        super(context);

        AnimInit(context, null);
    }

    public CameraFocusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        AnimInit(context, attrs);

    }

    public CameraFocusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        AnimInit(context, attrs);
    }

    private void AnimInit(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CameraFocusBar);
        long duration = array.getInt(R.styleable.CameraFocusBar_duration_focus, 5000);
        float fromScale = array.getFloat(R.styleable.CameraFocusBar_fromScale, 1.0f);
        float toScale = array.getFloat(R.styleable.CameraFocusBar_toScale, 0.5f);
        array.recycle();

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", fromScale, toScale);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setDuration(duration);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", fromScale, toScale);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setDuration(duration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleY).with(scaleX);
        animatorSet.start();
    }
}
