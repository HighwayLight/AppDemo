package com.example.myapplication.scroll;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by dushu on 2021/3/3 .
 */
public class CustomScrollView extends ScrollView {


    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.i("CustomScrollView", "onScrollChange, scrollY = "  + scrollY + "/oldScrollY = " + oldScrollY);
                }
            });
        }

    }

    protected boolean dispatchChildrenTouchEvent(MotionEvent ev) {
        Log.e("CustomScrollView", "dispatchChildrenTouchEvent");
        int n = getChildCount();
        while(n >0) {
            n --;
            View v = getChildAt(n);

            if(v.getVisibility() != View.VISIBLE) {
                continue;
            }

            if(!v.dispatchTouchEvent(ev)) {
                break;
            }
        }

        return true;
    }

    protected float _preTouchX;
    protected float _preTouchY;
    protected boolean _scrolling = false;
    protected boolean _cancelScrolling = false;
    protected boolean _tracking = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean frag = super.onInterceptTouchEvent(ev);
        Log.e("CustomScrollView", "return super.onInterceptTouchEvent(ev)"+ frag);
        return frag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean frag = super.onTouchEvent(ev);
        Log.e("CustomScrollView", "return super.onTouchEvent(ev)"+ frag);
        return frag;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction() & MotionEvent.ACTION_MASK){
//            case MotionEvent.ACTION_DOWN:
//                _preTouchX = ev.getX();
//                _preTouchY = ev.getY();
//                _tracking = true;
//                return super.dispatchTouchEvent(ev);
//            case MotionEvent.ACTION_MOVE:
//                if(_scrolling) {
//                    break;
//                }
//                float dx = Math.abs(ev.getX() - _preTouchX);
//                float dy = Math.abs(ev.getY() - _preTouchY);
//                if (dx > dy) {
//                    return dispatchChildrenTouchEvent(ev);
//                } else if(_cancelScrolling) {
//                    return dispatchChildrenTouchEvent(ev);
//                } else if(!_scrolling && dy >= 15){
//                    Log.e("CustomScrollView", "MotionEvent.ACTION_MOVE_onScrolling();");
//                    _scrolling = true;
//                }
//
//                break;
//
//            case MotionEvent.ACTION_UP:
//                if(_scrolling) {
////                    ev.setAction(MotionEvent.ACTION_CANCEL);
//                    _scrolling = false;
//                    _tracking = false;
//                }
//                return super.dispatchTouchEvent(ev);
//            case MotionEvent.ACTION_CANCEL:
//                _scrolling = false;
//                _tracking = false;
//                Log.e("CustomScrollView", "MotionEvent.ACTION_CANCEL");
//                return super.dispatchTouchEvent(ev);
//        }
//
//
//        if(_scrolling &&  (ev.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
//            Log.e("CustomScrollView", "onTouchEvent(ev)");
//            onTouchEvent(ev);
//            return true;
//        }
        boolean frag = super.dispatchTouchEvent(ev);
        Log.e("CustomScrollView", "return super.dispatchTouchEvent(ev) = "  + frag);
        return frag;

    }
}
