package com.example.myapplication.gif;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by dushu on 2021/8/26 .
 */
public class Gif2Activity extends AppCompatActivity {

    public static final int IMAGE_VIDEO_PICKER_REQUEST_CODE = 101;

    public static final String RESULT_IMAGES = "images";
    public static final String RESULT_VIDEO = "video";
    public static final String RESULT_GIF = "gif";

    private GifImageView gifView3;

    private String activityStatus = "unknow";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_2);
        activityStatus = "onCreate";
        gifView3 = findViewById(R.id.gif2);

        gifView3.setOnClickListener(new View.OnClickListener() {
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


        if (GifActivity.gif != null) {
            gifView3.setImageDrawable(GifActivity.gif);
            Log.e("Gif2", "onCreate " + ", gif.Visible = " + GifActivity.gif.isVisible());
        }

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
            Log.e("Gif2Activity", "type = " + type);
            Log.e("Gif2Activity", "uri = " + uri);
            if (uri == null) {
                return;
            }
            String path = getRealPathFromURI(uri, RESULT_GIF);
            Log.e("Gif2Activity", "path = " + path);
            if (path.endsWith(".gif")) {
                Log.e("Gif2Activity", "type = .gif");
//                gifView.setImageURI(uri);
                try {
                    File file = new File(path);
                    Log.e("Gif2Activity", ".gif.length = " + file.length()/1024 + "kb");
                    if (file.length() > 5 * 1024 * 1024) {
                    }

                    gifView3.setImageDrawable(new GifDrawable(path));
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
    protected void onDestroy() {
        super.onDestroy();
        activityStatus = "onDestroy";
//        GifActivity.multiCallback.removeView(gifView3);
//        GifActivity.gif.setCallback(GifActivity.multiCallback);

        Log.e("Gif2", "onDestroy " + ", gif.Visible = " + GifActivity.gif.isVisible());

    }

    @Override
    protected void onStart() {
        super.onStart();
        activityStatus = "onStart";
        Log.e("Gif2", "onStart " + ", gif.Visible = " + GifActivity.gif.isVisible());

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityStatus = "onResume";
        Log.e("Gif2", "onResume " + ", gif.Visible = " + GifActivity.gif.isVisible());
        if (GifActivity.gif != null) {
            gifView3.setImageDrawable(GifActivity.gif);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        activityStatus = "onPause";
        Log.e("Gif2", "onPause " + ", gif.Visible = " + GifActivity.gif.isVisible());
        if (GifActivity.gif != null) {
            gifView3.setImageDrawable(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStatus = "onStop";
        Log.e("Gif2", "onStop " + ", gif.Visible = " + GifActivity.gif.isVisible());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activityStatus = "onRestart";
        Log.e("Gif2", "onRestart " + ", gif.Visible = " + GifActivity.gif.isVisible());
    }
}
