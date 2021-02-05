package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    View view;

    int screenWidth ; // 屏幕宽（像素，如：480px）
//    int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
//                getAlbum();
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, CameraActivity.class));
                startActivity(new Intent(MainActivity.this, CameraXActivity.class));
            }
        });

        findViewById(R.id.button_x).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraX2Activity.class));
            }
        });

        findViewById(R.id.buttonScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CameraX2ScanActivity.class));
            }
        });
        editText = findViewById(R.id.editText);
        editText.setGravity(Gravity.CENTER);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        view = findViewById(R.id.view);
//        Drawable _backgroundImage = getResources().getDrawable(R.mipmap.car);
//        CustomDrawable customDrawable = new CustomDrawable(_backgroundImage);
        CustomDrawable customDrawable = new CustomDrawable(screenWidth, Dp2Px(this, 200));
        view.setBackground(customDrawable);




    }

    private void getAlbum() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mBaseUri = MediaStore.Files.getContentUri("external");
                Log.e("TAG", "mBaseUri = " + mBaseUri);
                String[] PROJECTION_BUCKET = {
                        MediaStore.Images.ImageColumns.BUCKET_ID,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                };
                /** where子句 */
                String mSelection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                Cursor cursor = getContentResolver().query(mBaseUri, PROJECTION_BUCKET, mSelection, null, null);
                if (cursor == null) {
                    Log.e("TAG", "cursor == null ");
                    return;
                }
                if (cursor.moveToFirst()) {
                    while (cursor.moveToNext()) {
                        long bucket_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID));
                        Log.e("TAG", "bucket_id = " + bucket_id);

//                        int media_type = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
//                        Log.e("TAG", "media_type = " + media_type);

//                        int parent = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT));
//                        Log.e("TAG", "parent = " + parent);

                        String bucket_display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                        Log.e("TAG", "bucket_display_name = " + bucket_display_name);


                    }
                } else {
                    Log.e("TAG", "cursor is null ");
                }

            }
        }).start();

    }


    private void selectImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = getContentResolver().query(mImageUri, null, null, null, null);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));// 1.获取图片的路径
//                    Log.e("TAG", path);

                    int photoIDIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    long photoId = cursor.getLong(photoIDIndex);
//                    Log.e("TAG", "_ID = " + photoId);

                    String bucket_display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    long bucket_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
//                    Log.e("TAG", "bucket_id = " + bucket_id);

                    getThumb(photoId);


                }
                cursor.close();
            }
        }).start();
    }

    private String getFolderPath(String filePath) {
        int endIndex = filePath.lastIndexOf(File.separator);
        return filePath.substring(0, endIndex);
    }


    private void getThumb(long photoId) {

        String[] projection = {MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};

        String mSelection = MediaStore.Images.Thumbnails.IMAGE_ID + " = " + photoId;

        long timecurrentTimeMillis = System.currentTimeMillis();
        Cursor cur = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection,
                mSelection,
                null,
                null);
        Log.e("TAG", "01 " + (System.currentTimeMillis() - timecurrentTimeMillis));

//        Cursor cur = MediaStore.Images.Thumbnails.queryMiniThumbnail(getContentResolver(),
//                photoId, MediaStore.Images.Thumbnails.MINI_KIND,
//                new String[]{
//                        MediaStore.Images.Thumbnails.IMAGE_ID,
//                        MediaStore.Images.Thumbnails.DATA
//                }
//        );
        if (cur != null) {
            if (cur.moveToFirst()) {
                long IMAGE_ID = cur.getLong(cur.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID));
                Log.e("TAG", "IMAGE_ID：" + IMAGE_ID);
                String path = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));// 1.获取图片的路径
                Log.e("TAG", "path：" + path);

                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail
                        (getContentResolver(), photoId, MediaStore.Images.Thumbnails.MINI_KIND, null);
                Log.e("TAG", "03 " + (System.currentTimeMillis() - timecurrentTimeMillis));
                BitmapDrawable drawable = new BitmapDrawable(getResources(),bitmap);

            } else {
//                long timecurrentTimeMillis = System.currentTimeMillis();
//                Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail
//                        (getContentResolver(), photoId, MediaStore.Images.Thumbnails.MINI_KIND, null);
//                Log.e("TAG", "生成缩略图耗时：" + (System.currentTimeMillis() - timecurrentTimeMillis));
//                getThumb(photoId);
            }
        }
        cur.close();
    }

    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}