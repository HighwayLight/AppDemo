package com.example.myapplication.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

/**
 * Create by zhongyao on 2021/2/1
 * Description:自定义拍照/录视频按钮
 */
public class CameraButton extends View {

    private static final String TAG = "CameraView";
    /**
     * 画笔
     */
    private Paint mBigCirclePaint;
    private Paint mSmallCirclePaint;
    private Paint mProgressCirclePaint;
    private Shader mShader;

    /**
     * 色值
     */
    private final String mBigCircleColor = "#36000000";
    private final String mSmallCircleStartColor = "#CB91EF";
    private final String mSmallCircleEndColor = "#8286F7";
    private final String mProgressDefaultColor = "#8286F7";
    private int mHeight;//当前View的高
    private int mWidth;//当前View的宽

    private int mMinTime, mMaxTime, mProgressColor;
    private float mProgressWidth;
    private float mCurrentBigRadius = 100;
    private float mCurrentSmallRadius = 80;
    private ValueAnimator mValueAnimator;
    private ValueAnimator mProgressAnim;//圆弧进度变化
    private boolean mIsMaxTime = false;
    private boolean mIsRecording = false;
    private boolean mIsPressed;
    private long mStartTime;
    private long mEndTime;
    private static final int WHAT_LONG_CLICK = 1;
    private final int mInitBigRadius = 100;
    private final int mEndBigRadius = 100;
    private final int mInitSmallRadius = 80;
    private final int mEndSmallRadius = 40;
    private float mCurrentProgress;
    private final long mRecordCriticalTime = 500;


    public CameraButton(Context context) {
        super(context);
        init(context, null);
    }

    public CameraButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CameraButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CameraButton);
        mMinTime = array.getInt(R.styleable.CameraButton_minTime, 2000);
        mMaxTime = array.getInt(R.styleable.CameraButton_maxTime, 10000);
        mProgressColor = array.getColor(R.styleable.CameraButton_progressColor, Color.parseColor(mProgressDefaultColor));
        mProgressWidth = array.getDimension(R.styleable.CameraButton_progressWidth, 12f);
        array.recycle();


        mBigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigCirclePaint.setColor(Color.parseColor(mBigCircleColor));

        mSmallCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mProgressCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressCirclePaint.setColor(mProgressColor);

        mProgressAnim = ValueAnimator.ofFloat(0, 360f);
        mProgressAnim.setDuration(mMaxTime);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mShader = new LinearGradient(mWidth / 2, mHeight / 2 - mInitSmallRadius, mWidth / 2, mHeight / 2 + mInitSmallRadius, Color.parseColor(mSmallCircleStartColor), Color.parseColor(mSmallCircleEndColor), Shader.TileMode.CLAMP);

        mSmallCirclePaint.setShader(mShader);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mWidth / 2, mHeight / 2, mCurrentBigRadius, mBigCirclePaint);

        canvas.drawCircle(mWidth / 2, mHeight / 2, mCurrentSmallRadius, mSmallCirclePaint);



        if (mIsRecording) {
            drawProgress(canvas);
        }
    }

    private void drawProgress(Canvas canvas) {
        mProgressCirclePaint.setStyle(Paint.Style.STROKE);
        mProgressCirclePaint.setStrokeWidth(mProgressWidth);
        //用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF(mWidth / 2 - (mInitBigRadius - mProgressWidth / 2), mHeight / 2 - (mInitBigRadius - mProgressWidth / 2), mWidth / 2 + (mInitBigRadius - mProgressWidth / 2), mHeight / 2 + (mInitBigRadius - mProgressWidth / 2));
        //根据进度画圆弧
        canvas.drawArc(oval, -90, mCurrentProgress, false, mProgressCirclePaint);
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == WHAT_LONG_CLICK) {
                //长按事件触发
                if (onLongClickListener != null) {
                    onLongClickListener.onLongClick();
                }
                startAnimation(mInitBigRadius, mEndBigRadius, mInitSmallRadius, mEndSmallRadius);
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                mStartTime = System.currentTimeMillis();
                Message message = Message.obtain();
                message.what = WHAT_LONG_CLICK;
                mHandler.sendMessageDelayed(message, mRecordCriticalTime);
                break;

            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                mEndTime = System.currentTimeMillis();
                if (mEndTime - mStartTime < mRecordCriticalTime) {
                    //如果按压时间小于临界值，则将消息队列中的该条消息清理掉
                    mHandler.removeMessages(WHAT_LONG_CLICK);
                    if (onClickListener != null) {
                        onClickListener.onClick();
                    }
                } else {
                    //如果按压时间大于临界值，表示此时已进行了视频录制操作，抬手时动画需复原
                    startAnimation(mCurrentBigRadius, mInitBigRadius, mCurrentSmallRadius, mInitSmallRadius);//手指离开时动画复原
                    Log.d(TAG, "mProgressAnim.getCurrentPlayTime() :" + mProgressAnim.getCurrentPlayTime());
                    if (mProgressAnim != null && mProgressAnim.getCurrentPlayTime() < mMinTime && !mIsMaxTime) {
                        if (onLongClickListener != null) {
                            onLongClickListener.onNoMinRecord(mMinTime);
                        }
                        mProgressAnim.cancel();
                    } else {
                        //录制完成
                        cancelProgressAnimation();
                        if (onLongClickListener != null && !mIsMaxTime) {
                            onLongClickListener.onRecordFinishedListener();
                        }
                    }
                }

                break;

            default:
                break;
        }

        return true;
    }

    private void startAnimation(float startBigRadius, float endBigRadius, float startSmallRadius, float endSmallRadius) {

//        ValueAnimator bigObjAnim = ValueAnimator.ofFloat(startBigRadius, endBigRadius);
//        bigObjAnim.setDuration(150);
//        bigObjAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                mCurrentBigRadius = (float) animation.getAnimatedValue();
//                invalidate();
//            }
//        });

        ValueAnimator smallObjAnim = ValueAnimator.ofFloat(startSmallRadius, endSmallRadius);
        smallObjAnim.setDuration(150);
        smallObjAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentSmallRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

//        bigObjAnim.start();
        smallObjAnim.start();

        smallObjAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mIsRecording = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //开始绘制圆形进度
                if (mIsPressed) {
                    mIsRecording = true;
                    mIsMaxTime = false;
                    startProgressAnimation();
                }
            }
        });
    }

    private void startProgressAnimation() {
        mProgressAnim.start();
        mProgressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mProgressAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                //录制动画结束时，即为录制全部完成
                if (onLongClickListener != null && mIsPressed) {
                    mIsPressed = false;
                    mIsMaxTime = true;
                    onLongClickListener.onRecordFinishedListener();
                    startAnimation(mCurrentBigRadius, mInitBigRadius, mCurrentSmallRadius, mInitSmallRadius);
                    //隐藏进度条进度
                    mCurrentProgress = 0;
                    invalidate();
                }
            }
        });
    }

    private void cancelProgressAnimation() {
        if (mProgressAnim != null) {
            mCurrentProgress = 0;
            invalidate();
            mProgressAnim.cancel();
        }
    }

    /**
     * 长按监听器
     */
    public interface OnLongClickListener {
        void onLongClick();

        //未达到最小录制时间
        void onNoMinRecord(int currentTime);

        //录制完成
        void onRecordFinishedListener();
    }

    public OnLongClickListener onLongClickListener;

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    /**
     * 点击监听器
     */
    public interface OnClickListener {
        void onClick();
    }

    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
