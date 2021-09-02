package com.example.myapplication.PhotoAlbum;

import static com.example.myapplication.PhotoAlbum.PhotoAlbumActivity.maxNum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.PhotoAlbum.loader.Folder;
import com.example.myapplication.PhotoAlbum.loader.MediaEntity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dushu on 2021/8/31 .
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_FOOTER = 1;  //底部FootView

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //上拉加载更多状态-默认为0
    private int load_more_status = 0;

    private List<MediaEntity> items = new ArrayList();
    private List<Folder> folders = new ArrayList();
    private LayoutInflater mInflater;

    private String MediaType;
    private Context context;
    private int itemSelectNum = 0;//多选模式下 选中的item的个数
    private HashMap<Integer, String> imageSelected = new HashMap<>();

    // 存储勾选框状态的map集合  解决滑动的复用问题 存储选中状态
    private SparseBooleanArray array = new SparseBooleanArray();

    public PhotoAdapter(Context context) {
        this.items.addAll(items);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        MediaType = context.getResources().getString(R.string.tma_all_dir_name);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View view = mInflater.inflate(R.layout.item_photo, parent, false);
            ItemViewHolder holder = new ItemViewHolder(view);
            //动态设置item 的高度
            float width = getScreenWidth(context);
            width = width / 4;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.imageView.getLayoutParams();
            layoutParams.height = (int) width;
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = mInflater.inflate(R.layout.recycler_load_more_layout, parent, false);
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到预览页
                    Toast.makeText(context, "imageView.onclick()", Toast.LENGTH_SHORT).show();
                }
            });


            vh.iconSelect.setTag(position);
            vh.iconSelect.setMultiSelect(PhotoAlbumActivity.isMultiSelect);

            vh.iconSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int posi = (int) v.getTag();
                    if (vh.iconSelect.isMultiSelect()) {
                        //多选
                        if (vh.iconSelect.isSelected()) {
                            array.delete(posi);
                            itemSelectNum--;
                            imageSelected.remove(vh.iconSelect.getIndex());
                            items.get(posi).itemIndex = 0;
                            vh.iconSelect.setSelectStatus(false, 0);
                            items.get(posi).isSelected = false;
                            if (array.size() == maxNum - 1) {
                                notifyDataSetChanged();
                            }
                        } else {
                            if (imageSelected.size() == maxNum || array.size() == maxNum) {
                                Toast.makeText(context, "最多选择" + maxNum + "张图片", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            array.put(posi, true);
                            itemSelectNum++;
                            items.get(posi).itemIndex = itemSelectNum;
                            vh.iconSelect.setSelectStatus(true, items.get(posi).itemIndex);
                            items.get(posi).isSelected = true;
                            imageSelected.put(vh.iconSelect.getIndex(), items.get(posi).URI);
                            if (array.size() == maxNum) {
                                notifyDataSetChanged();
                            }
                        }
                        //更新发送按钮上的文本状态
                        if (context instanceof PhotoAlbumActivity) {
                            ((PhotoAlbumActivity) context).updateSendBtnText(String.valueOf(itemSelectNum));
                        }
                    } else {
                        //单选
                        if (vh.iconSelect.isSelected()) {
                            vh.iconSelect.setSelectStatus(false);
                            array.delete(posi);
                            items.get(posi).isSelected = false;
                            imageSelected.remove(vh.iconSelect.getIndex());
                        } else {
                            if (imageSelected.size() == 1 || array.size() == 1) {
                                Toast.makeText(context, "最多选择一张图片", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            vh.iconSelect.setSelectStatus(true);
                            array.put(posi, true);
                            items.get(posi).isSelected = true;
                            imageSelected.put(vh.iconSelect.getIndex(), items.get(posi).URI);
                        }
                        notifyDataSetChanged();
                    }
//                    notifyDataSetChanged();
                }
            });

            Glide.with(context).asBitmap().load(items.get(position).URI).into(vh.imageView);

            if (!PhotoAlbumActivity.isMultiSelect) {
                vh.iconSelect.setSelectStatus(array.get(position));
                //单选模式
                if (items.get(position).isSelected) {
                    //当前选中的item
                    vh.cover.setBackgroundColor(context.getResources().getColor(R.color.color_26FFFFFF));
                } else if (!items.get(position).isSelected && imageSelected.size() == 1) {
                    //选中状态下 当前未被选中的item
                    vh.cover.setBackgroundColor(context.getResources().getColor(R.color.color_1A000000));
                } else {
                    //未选中状态
                    vh.cover.setBackgroundColor(0);
                }
            } else {
                //多选模式
                vh.iconSelect.setSelectStatus(array.get(position), items.get(position).itemIndex);
                if (array.size() == maxNum) {
                    if (items.get(position).isSelected) {
                        //当前选中的item
                        vh.cover.setBackgroundColor(context.getResources().getColor(R.color.color_26FFFFFF));
                    } else if (!items.get(position).isSelected) {
                        //选中maxNum状态下 当前未被选中的item
                        vh.cover.setBackgroundColor(context.getResources().getColor(R.color.color_1A000000));
                    }
                } else if (array.size() < maxNum) {
                    if (items.get(position).isSelected) {
                        //当前选中的item
                        vh.cover.setBackgroundColor(context.getResources().getColor(R.color.color_26FFFFFF));
                    } else {
                        //未选中状态
                        vh.cover.setBackgroundColor(0);
                    }
                }
            }


        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (load_more_status) {
                case PULLUP_LOAD_MORE:
                    footViewHolder.foot_view_item_tv.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footViewHolder.foot_view_item_tv.setText("正在加载更多数据...");
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public void setItems(List<Folder> folders) {
        this.folders.clear();
        this.folders.addAll(folders);
        for (int i = 0; i < folders.size(); i++) {
            if (TextUtils.equals(MediaType, folders.get(i).name)) {
                items.clear();
                items.addAll(folders.get(i).getMedias());
                notifyDataSetChanged();
                return;
            }
        }
        try {
            if (items.isEmpty()) {
                items.addAll(folders.get(0).getMedias());
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setMediaType(String MediaType) {
        this.MediaType = MediaType;
        for (int i = 0; i < folders.size(); i++) {
            if (TextUtils.equals(MediaType, folders.get(i).name)) {
                items.clear();
                items.addAll(folders.get(i).getMedias());
                notifyDataSetChanged();
                return;
            }
        }
    }

    public void addMoreItem(List<MediaEntity> newDatas) {
        items.addAll(newDatas);
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        //        SelectIconView iconSelect;
        CheckView iconSelect;
        View cover;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            iconSelect = itemView.findViewById(R.id.icon_select);
            cover = itemView.findViewById(R.id.v_cover);
        }


    }

    /**
     * 底部FootView布局
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {

        private TextView foot_view_item_tv;

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            foot_view_item_tv = itemView.findViewById(R.id.foot_view_item_tv);
        }
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null || !(layoutManager instanceof GridLayoutManager)) return;
        final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
        int spanCount = gridLayoutManager.getSpanCount();
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getItemViewType(position) == TYPE_FOOTER) {
                    return spanCount;
                }
                return 1;
            }
        });

    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
}
