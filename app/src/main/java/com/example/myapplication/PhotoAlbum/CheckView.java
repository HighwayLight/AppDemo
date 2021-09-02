package com.example.myapplication.PhotoAlbum;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.myapplication.R;

/**
 * Created by dushu on 2021/9/1 .
 * CheckView
 */
public class CheckView extends FrameLayout {

    private boolean isSelected = false;//选中状态
    @DrawableRes
    private int unSelectIconID = R.mipmap.icon_unselect;

    @DrawableRes
    private int selectedIconID = 0;

    private int index = 0;

    private boolean isMultiSelect = false;//单选/多选标记位

    private Drawable drawable;


    private AppCompatImageView ivCheck;
    private TextView tvIndex;

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SelectIconView);
        int unSelectedID = array.getResourceId(R.styleable.SelectIconView_unSelect_icon_id, 0);
        if (unSelectedID != 0) {
            this.unSelectIconID = unSelectedID;
        }
        selectedIconID = array.getResourceId(R.styleable.SelectIconView_selected_icon_id, 0);

        //使用布局资源填充视图
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //加载布局文件
        mInflater.inflate(R.layout.view_check, this, true);
        this.ivCheck = findViewById(R.id.iv_check);
        this.tvIndex = findViewById(R.id.tv_check_index);
        this.ivCheck.setImageResource(unSelectIconID);
        drawable = context.getDrawable(R.drawable.shape_check_bg);
    }


    public boolean isSelected() {
        return isSelected;
    }

    /**
     * 设置选中状态（单选模式）
     *
     * @param isSelected
     */
    public void setSelectStatus(boolean isSelected) {
        this.isSelected = isSelected;
        if (isMultiSelect) {
            return;
        }
        if (this.isSelected) {
            this.ivCheck.setImageResource(selectedIconID);
        } else {
            this.ivCheck.setImageResource(unSelectIconID);
        }
    }

    /**
     * 设置选中状态（多选模式）
     *
     * @param isSelected
     */
    public void setSelectStatus(boolean isSelected, int index) {
        this.isSelected = isSelected;
        if (this.isSelected) {
            this.index = index;
            this.ivCheck.setImageDrawable(drawable);
            this.tvIndex.setText(String.valueOf(index));
        } else {
            this.ivCheck.setImageResource(unSelectIconID);
            this.index = 0;
            this.tvIndex.setText(null);
        }

    }

    public int getIndex() {
        return this.index;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setMultiSelect(boolean isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
    }
}
