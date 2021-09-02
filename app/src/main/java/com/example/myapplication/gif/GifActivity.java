package com.example.myapplication.gif;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.MultiCallback;

/**
 * Created by dushu on 2021/8/19 .
 */
public class GifActivity extends AppCompatActivity {

    /**
     * 图片(视频)选择requestCode
     */
    public static final int IMAGE_VIDEO_PICKER_REQUEST_CODE = 100;

    public static final String RESULT_IMAGES = "images";
    public static final String RESULT_VIDEO = "video";
    public static final String RESULT_GIF = "gif";

    private GifImageView gifView;
    private GifImageView gifView2;

    public static CustomGifDrawable gif;
    public static MultiCallback multiCallback;

    String path;
    public String activityStatus = "unknow";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
        activityStatus = "onCreate";
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                XXPermissions.with(GifActivity.this).permission(PermissionUtil.PERMISSION_STORAGE)
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                intent.setType("*/*");
                intent.setType("image/*;video/*");
                startActivityForResult(intent, IMAGE_VIDEO_PICKER_REQUEST_CODE);
            }
        });

        gifView = findViewById(R.id.gif_image_view);
        gifView2 = findViewById(R.id.gif_image_view2);

        findViewById(R.id.jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.setImageDrawable(null);
                startActivity(new Intent(GifActivity.this, Gif2Activity.class));
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_VIDEO_PICKER_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            String type = data.getType();
            if (uri == null) {
                return;
            }
            path = getRealPathFromURI(uri, RESULT_GIF);
            if (path.endsWith(".gif")) {
//                gifView.setImageURI(uri);
                try {
                    File file = new File(path);
                    Log.e("GifActivity", ".gif.length = " + file.length() / 1024 + "kb");
                    if (file.length() > 5 * 1024 * 1024) {
                    }
                    multiCallback = new MultiCallback();

                    gif = new CustomGifDrawable(path, this);

                    gifView.setImageDrawable(gif);
//                    multiCallback.addView(gifView);
//                    gif.setCallback(multiCallback);


//                    gifView2.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            gifView2.setImageDrawable(gif);
//                            multiCallback.addView(gifView2);
//
//                            gif.setCallback(multiCallback);
//                        }
//                    }, 500);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private String getRealPathFromURI(Uri contentURI, String type) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentURI, null, null, null, null);
        //不能直接调用contentProvider的接口函数，需要使用contentResolver对象，通过URI间接调用contentProvider
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = 0;
            if (type.equals(RESULT_IMAGES) || type.equals(RESULT_GIF)) {
                idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            } else if (type.equals(RESULT_VIDEO)) {
                idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            }
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activityStatus = "onRestart";
        Log.e("Gif", "onRestart, gif.Visible = " + gif.isVisible());
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityStatus = "onResume";
        if (gif != null) {
            Log.e("Gif", "onResume, gif.Visible = " + gif.isVisible());
            if (gifView.getDrawable() == null) {
                gifView.setImageDrawable(gif);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityStatus = "onStart";
    }



    @Override
    protected void onPause() {
        super.onPause();
        activityStatus = "onPause";
        if (gif != null) {
            Log.e("Gif", "onPause, gif.Visible = " + gif.isVisible());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStatus = "onStop";
        if (gif != null) {
            Log.e("Gif", "onStop, gif.Visible = " + gif.isVisible());
        }
    }


}
