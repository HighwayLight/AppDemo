package com.example.myapplication.PhotoAlbum;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatImageView;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

/**
 * Created by dushu on 2021/9/1 .
 * 带选中状态的ImageView
 */
public class SelectIconView extends AppCompatImageView {

    private boolean isSelected = false;//选中状态
    @DrawableRes
    private int unSelectIconID = R.mipmap.icon_unselect;

    @DrawableRes
    private int selectedIconID = 0;

    private int index = 0;

    private boolean isMultiSelect = false;//单选/多选标记位

    private SelectedDrawable drawable;

    public SelectIconView(Context context) {
        this(context, null);
    }

    public SelectIconView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SelectIconView);
        int unSelectedID = array.getResourceId(R.styleable.SelectIconView_unSelect_icon_id, 0);
        if (unSelectedID != 0) {
            this.unSelectIconID = unSelectedID;
        }
        selectedIconID = array.getResourceId(R.styleable.SelectIconView_selected_icon_id, 0);
        this.setImageResource(unSelectIconID);
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
            this.setImageResource(selectedIconID);
        } else {
            this.setImageResource(unSelectIconID);
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
//            drawable = new SelectedDrawable(this.getWidth(), this.index,
//                    dip2px(getContext(), 20f));
//            if (drawable != null) {
//                this.setImageDrawable(drawable);
//            }
            this.setImageDrawable(new SelectedDrawable(this.getWidth(), this.index,
                    dip2px(getContext(), 20f)));
        } else {
            this.setImageResource(unSelectIconID);
            this.index = 0;
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
