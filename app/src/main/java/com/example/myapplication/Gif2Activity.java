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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_2);

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
//            gifView3.setImageDrawable(GifActivity.gif);
//            GifActivity.multiCallback.addView(gifView3);
//            GifActivity.gif.setCallback(GifActivity.multiCallback);
            gifView3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gifView3.setImageDrawable(GifActivity.gif);
                    GifActivity.multiCallback.addView(gifView3);
                    GifActivity.gif.setCallback(GifActivity.multiCallback);
                }
            },80);

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
        GifActivity.multiCallback.removeView(gifView3);
        GifActivity.gif.setCallback(GifActivity.multiCallback);
    }
}
