package com.example.myapplication.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.myapplication.R;

/**
 * Create by zhongyao on 2021/2/2
 * Description: 相机扫一扫动画条
 */
public class CameraScanBar extends AppCompatImageView {
    private AnimatorSet mAnimatorSet;

    public CameraScanBar(@NonNull Context context) {
        super(context);

        AnimInit(context, null);
    }

    public CameraScanBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        AnimInit(context, attrs);
    }

    public CameraScanBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        AnimInit(context, attrs);
    }

    private void AnimInit(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CameraScanBar);
        long duration = array.getInt(R.styleable.CameraScanBar_duration, 3000);
        float startY = array.getDimension(R.styleable.CameraScanBar_startY, 0);
        float endY = array.getDimension(R.styleable.CameraScanBar_endY, 1000);
        float startAlpha = array.getFloat(R.styleable.CameraScanBar_startAlpha, 0.2f);
        float endAlpha = array.getFloat(R.styleable.CameraScanBar_endAlpha, 1.0f);


        ObjectAnimator yAnim = ObjectAnimator.ofFloat(this, "Y", startY, endY);
        yAnim.setRepeatCount(ValueAnimator.INFINITE);
        yAnim.setDuration(duration);

        ObjectAnimator alphaAnimIn = ObjectAnimator.ofFloat(this, "alpha", startAlpha, endAlpha, endAlpha, endAlpha, startAlpha);
        alphaAnimIn.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimIn.setDuration(duration);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(yAnim).with(alphaAnimIn);
        mAnimatorSet.start();
    }

    /**
     * 退出时取消动画
     */
    public void cancelScanAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }
}
