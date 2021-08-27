package com.example.myapplication;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.XXPermissions;

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

    public static GifDrawable gif;
    public static MultiCallback multiCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

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

                startActivity(new Intent(GifActivity.this, Gif2Activity.class));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_VIDEO_PICKER_REQUEST_CODE){
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            String type = data.getType();
            Log.e("GifActivity", "type = " + type);
            Log.e("GifActivity", "uri = " + uri);
            if (uri == null) {
                return;
            }
            String path = getRealPathFromURI(uri, RESULT_GIF);
            Log.e("GifActivity", "path = " + path);
            if (path.endsWith(".gif")) {
                Log.e("GifActivity", "type = .gif");
//                gifView.setImageURI(uri);
                try {
                    File file = new File(path);
                    Log.e("GifActivity", ".gif.length = " + file.length()/1024 + "kb");
                    if (file.length() > 5 * 1024 * 1024) {
                    }
                    multiCallback = new MultiCallback();

                    gif = new GifDrawable(path);

                    gifView.setImageDrawable(gif);
                    multiCallback.addView(gifView);
                    gif.setCallback(multiCallback);


                    gifView2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gifView2.setImageDrawable(gif);
                            multiCallback.addView(gifView2);

                            gif.setCallback(multiCallback);
                        }
                    },500);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gif != null) {
            Log.e("Gif", "gif.isRunning() = " + gif.isRunning());
//            gif.setVisible(true, true);
//            gifView.setImageDrawable(null);
//            gifView.setImageDrawable(gif);
//            multiCallback.addView(gifView);
//
//            gifView2.setImageDrawable(null);
//            gifView2.setImageDrawable(gif);
//            multiCallback.addView(gifView2);
//
//            gif.setCallback(multiCallback);
        }
    }
}
