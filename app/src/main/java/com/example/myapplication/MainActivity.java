package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.PhotoAlbum.PhotoAlbumActivity;
import com.example.myapplication.PhotoAlbum.SelectedDrawable;
import com.example.myapplication.PhotoAlbum.loader.DataCallback;
import com.example.myapplication.PhotoAlbum.loader.Folder;
import com.example.myapplication.decode.ImageUtil;
import com.example.myapplication.gif.GifActivity;
import com.example.myapplication.scroll.ScrollActivity;
import com.example.myapplication.video.VideoActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.PhotoAlbum.loader.LoaderM.LOADER_ID;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    View view;

    int screenWidth; // 屏幕宽（像素，如：480px）
    //    int screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
    int pageindex = 0;

    ImageView imageView;
    ImageView ivDrawable;

    boolean flag = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MTextView textView = findViewById(R.id.text1);
        textView.setMText("你好，欢迎来到ZAO PARK。ZAO代表着一个人沉浸在TA的爱好时的状态：zealous（热情）、" +
                "absolute（纯粹）、optimistic（乐观）。你可以加入或创建不同的兴趣圈子，和有志趣相投的人畅聊爱好，分享快乐。你也可以结识同好，" +
                "通过兴趣认识新朋友。首先，请加入ZAO PARK官方圈子，我们需要你的鼓励，更需要你的建议。https://zao.place.fun/h/374676837286019272");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                imageLoader();
                selectImage();
//                getAlbum();
            }
        });
        ivDrawable = findViewById(R.id.iv_drawable);
        SelectedDrawable drawable = new SelectedDrawable(15, 1, 8);
        ivDrawable.setImageDrawable(drawable);


        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CustomViewActivity.class));
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
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.hideKeyboard(MainActivity.this);
                if (!flag) {
                    pauseMusic();
                    flag = true;
                } else {
                    replayMusic();
                    flag = false;
                }
            }
        });

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        view = findViewById(R.id.view);
        Drawable _backgroundImage = getResources().getDrawable(R.mipmap.car);
        CustomDrawable customDrawable = new CustomDrawable(_backgroundImage);
//        CustomDrawable customDrawable = new CustomDrawable(screenWidth, Dp2Px(this, 200));
        view.setBackground(customDrawable);
//        view.setBackground(drawable);
//        ivDrawable.setImageDrawable(customDrawable);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScrollActivity.class));
            }
        });
        findViewById(R.id.button_v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
            }
        });

        findViewById(R.id.button_f).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
            }
        });

//        findViewById(R.id.btn_service).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                startActivity(intent);
//            }
//        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GifActivity.class));
            }
        });

        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhotoAlbumActivity.class));
            }
        });
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
//        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC limit " + 2 + " offset " + pageindex * 2;
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC limit " + 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Cursor cursor = getContentResolver().query(mImageUri, null, null, null, null);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));// 1.获取图片的路径
                    Log.e("TAG", path);

                    int photoIDIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    long photoId = cursor.getLong(photoIDIndex);
//                    Log.e("TAG", "_ID = " + photoId);

                    String bucket_display_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    long bucket_id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
//                    Log.e("TAG", "bucket_id = " + bucket_id);

//                    getThumb(photoId);
                    Drawable drawable = null;
                    if (drawable == null) {
                        try {
                            drawable = ImageUtil.getImageDrawable(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("TAG", e.toString());
                        }
                    }
                    if (drawable != null) {
                        Drawable finalDrawable = drawable;
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageDrawable(finalDrawable);
                            }
                        });
                    } else {
                        Log.e("TAG", "drawable = null");
                    }

                }
                cursor.close();
            }
        }).start();
        pageindex++;
    }

    private void imageLoader() {

//        this.getLoaderManager().initLoader(LOADER_ID, null,
//                new ImageLoader(this, "all", 1, pageindex, 2, new DataCallback() {
//                    @Override
//                    public void onData(List list) {
//                        Log.e("ZKMediaLibrary", "queryMedia() / onData().list.size() = "
//                                + (list == null ? "null" : list.size()));
//                    }
//
//                    @Override
//                    public void onData(ArrayList<Folder> list) {
//
//                    }
//                }));
//        pageindex++;
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
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
//                if (drawable != null) {
//                    imageView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            imageView.setImageDrawable(drawable);
//                        }
//                    });
//                }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡不可用~", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                } else {
                    //这里就是权限打开之后自己要操作的逻辑
                }
            }
        }
    }


    /**
     * 隐藏键盘的方法
     *
     * @param context
     */
    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
    }


    private void pauseMusic() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    private void replayMusic() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(null);
    }

}