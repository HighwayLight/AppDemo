package com.example.myapplication.PhotoAlbum;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.PhotoAlbum.loader.DataCallback;
import com.example.myapplication.PhotoAlbum.loader.Folder;
import com.example.myapplication.PhotoAlbum.loader.ImageLoader;
import com.example.myapplication.PhotoAlbum.loader.MediaLoader;
import com.example.myapplication.PhotoAlbum.loader.VideoLoader;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dushu on 2021/8/31 .
 */
public class PhotoAlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PhotoAlbumActivity";
    RecyclerView mPhotoList;
    SwipeRefreshLayout refreshLayout;
    PhotoAdapter myAdapter;
    private int lastVisibleItem;


    private Map<Integer, String> imageSelected = new HashMap<>();
    public static boolean isMultiSelect = true;//单选/多选标记位
    public static int maxNum = 3;

    public boolean isOriginalImg = false;//原图状态标记位

    public static final int PICKER_IMAGE = 100;
    public static final int PICKER_VIDEO = 102;
    public static final int PICKER_IMAGE_VIDEO = 101;

    private TextView tvCancel;
    private TextView tvFolderTitle;
    private ImageView iconFolderStatus;
    private TextView tvPreview;
    private LinearLayout lyOriginal;
    private CheckView iconOriSelectStatus;
    private Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);
        initStatusBar();


        initPhotoList();
        initView();
        getAllMedia();
//        getImages();
//        getVideo();
//        isMultiSelect = false;
    }
    //沉浸式状态栏
    private void initStatusBar(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void initView() {
        lyOriginal = findViewById(R.id.ly_original);
        tvCancel = findViewById(R.id.tv_cancel);
        iconOriSelectStatus = findViewById(R.id.icon_ori_select_status);
        btnSend = findViewById(R.id.btn_send);
        tvFolderTitle = findViewById(R.id.tv_folder_title);
        iconFolderStatus = findViewById(R.id.icon_folder_status);
        tvPreview = findViewById(R.id.tv_preview);

        lyOriginal.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        iconOriSelectStatus.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        tvFolderTitle.setOnClickListener(this);
        iconFolderStatus.setOnClickListener(this);
        tvPreview.setOnClickListener(this);
    }

    /**
     * 初始化图片列表
     */
    private void initPhotoList(){
        mPhotoList = findViewById(R.id.photos_rc);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mPhotoList.setLayoutManager(manager);
        myAdapter = new PhotoAdapter(this);
        mPhotoList.setAdapter(myAdapter);
        mPhotoList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == myAdapter.getItemCount()) {
                    myAdapter.changeMoreStatus(PhotoAdapter.LOADING_MORE);
                    //加载更多
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            myAdapter.addMoreItem(newDatas);
//                            myAdapter.changeMoreStatus(MyAdapter.PULLUP_LOAD_MORE);
                        }
                    }, 2500);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });
        refreshLayout = findViewById(R.id.srl);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新
                refreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    public void getAllMedia(){
        getLoaderManager().initLoader(PICKER_IMAGE_VIDEO, null,
                new MediaLoader(this, new DataCallback() {
                    @Override
                    public void onData(ArrayList<Folder> list) {
                        for (Folder folder : list) {
                            Log.e(TAG, "Media__folder.name = " + folder.name + " , folder.medias.size() = "  +folder.getMedias().size());
                        }
                        myAdapter.setItems(list);
                    }
                }));
    }

    public void getImages() {
        getLoaderManager().initLoader(PICKER_IMAGE, null,
                new ImageLoader(this, new DataCallback() {
                    @Override
                    public void onData(ArrayList<Folder> list) {
                        for (Folder folder : list) {
                            Log.e(TAG, "Image__folder.name = " + folder.name + " , folder.medias.size() = "  +folder.getMedias().size());
                        }
                        myAdapter.setItems(list);
                    }
                }));
    }

    public void getVideo(){
        getLoaderManager().initLoader(PICKER_VIDEO, null,
                new VideoLoader(this, new DataCallback() {
                    @Override
                    public void onData(ArrayList<Folder> list) {
                        for (Folder folder : list) {
                            Log.e(TAG, "Video__folder.name = " + folder.name + " , folder.medias.size() = "  +folder.getMedias().size());
                        }

                    }
                }));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.isMultiSelect = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_original:
            case R.id.icon_ori_select_status:
                if (iconOriSelectStatus.isSelected()) {
                    iconOriSelectStatus.setSelectStatus(false);
                } else {
                    iconOriSelectStatus.setSelectStatus(true);
                }
                this.isOriginalImg = iconOriSelectStatus.isSelected();
                break;
            case R.id.tv_folder_title:
            case R.id.icon_folder_status:
                //切换文件夹




                break;
            case R.id.tv_cancel:
                this.finish();
                break;
            case R.id.btn_send:
                //发送

                break;

            case R.id.tv_preview:
                //预览

                break;
        }
    }
    /**
     *  更新发送按钮上的文本状态
     */
    public void updateSendBtnText(String text){
        if (btnSend != null) {
            StringBuilder sb = new StringBuilder("发送(");
            sb.append(text);
            sb.append(")");
            btnSend.setText(sb.toString());
        }
    }
}
