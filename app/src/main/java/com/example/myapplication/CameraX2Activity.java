package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.VideoCapture;
import androidx.camera.view.video.OnVideoSavedCallback;
import androidx.camera.view.video.OutputFileResults;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.camera.CameraXView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static androidx.camera.core.CameraX.getContext;

/**
 * Created by dushu on 2021/2/2 .
 */
public class CameraX2Activity extends AppCompatActivity {
    private static final String TAG = "CameraX2Activity";

    private static final String FILENAME_FORMATE = "yyyy-MM-DD-HH-mm-ss";

    CameraXView cameraXView;
    RecordButton recordButton;

    ImageAnalysis myAnalyzer;

    private ExecutorService cameraExecutor;
    private String outputFilePath;

    @SuppressLint({"MissingPermission", "UnsafeExperimentalUsageError"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax2);

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraXView = findViewById(R.id.camera_x_view);
        myAnalyzer = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        myAnalyzer.setAnalyzer(cameraExecutor, new LuminosityAnalyzer());
        cameraXView.bindToLifecycle(this, myAnalyzer);

        recordButton = findViewById(R.id.record_button);// 拍照按钮
        cameraXView.setCaptureMode(CameraXView.CaptureMode.IMAGE);
        recordButton.setOnRecordListener(new RecordButton.OnRecordListener() {
            @Override
            public void onTackPicture() {
//                cameraXView.setCaptureMode(CameraXView.CaptureMode.IMAGE);
                takephoto();
            }

            @Override
            public void onRecordVideo() {
//                cameraXView.setCaptureMode(CameraXView.CaptureMode.VIDEO);
                if (allPermissionGranted()) {
                    takeVideo();
                } else {
                    ActivityCompat.requestPermissions(CameraX2Activity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},11);
                }
            }

            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void onFinish() {
                if (cameraXView.isRecording()) {
                    cameraXView.stopRecording();
                }
            }
        });
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void takeVideo() {
        //创建视频保存的文件地址
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(),
                System.currentTimeMillis() + ".mp4");
        cameraXView.startRecording(file, cameraExecutor, new OnVideoSavedCallback() {


            @Override
            public void onVideoSaved(@NonNull OutputFileResults outputFileResults) {
                outputFilePath = file.getAbsolutePath();
                Log.e(TAG, "Video outputFilePath = " + outputFilePath);
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, outputFilePath);
                getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                Log.i(TAG, message);
            }
        });

    }

    private void takephoto() {
        //1 设置要保存的路径和文件名字
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "image-" + new SimpleDateFormat(FILENAME_FORMATE, Locale.CHINA).format(System.currentTimeMillis()) + ".jpg");
        //2 定义 拍摄imageCapture实例
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        cameraXView.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                //保存成功
                Uri savedUri = outputFileResults.getSavedUri();
                if (savedUri == null) {
                    savedUri = Uri.fromFile(photoFile);
                }
                outputFilePath = photoFile.getAbsolutePath();
                Log.e(TAG, "Photo outputFilePath = " + outputFilePath);
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            outputFilePath, photoFile.getName(), null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoFile.getPath())));
//                onFileSaved(savedUri);
                Log.e(TAG, "Photo capture success");
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                //"保存失败"+
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });

    }

    //将前面保存的文件添加到媒体中
    private void onFileSaved(Uri savedUri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri));
        }
        String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap
                .getFileExtensionFromUrl(savedUri.getPath()));
        MediaScannerConnection.scanFile(getApplicationContext(),
                new String[]{new File(savedUri.getPath()).getAbsolutePath()},
                new String[]{mimeTypeFromExtension}, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d(TAG, "Image capture scanned into media store: $uri" + uri);
                    }
                });


//        PreviewActivity.start(this, outputFilePath, !takingPicture);启动预览页面？
    }


    private boolean allPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED  ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
