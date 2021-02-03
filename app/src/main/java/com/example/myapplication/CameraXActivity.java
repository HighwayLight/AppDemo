package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraView;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dushu on 2021/2/1 .
 */
public class CameraXActivity extends AppCompatActivity {

    private static final String TAG = "CameraXActivity";
    private static final String FILENAME_FORMATE = "yyyy-MM-DD-HH-mm-ss";

    private ExecutorService cameraExecutor;
    private Preview preview;
    Camera camera;
    private PreviewView viewFinder;

    ProcessCameraProvider cameraProvider;
    RecordButton recordButton;
    ImageCaptureConfig imageCaptureConfig;
    private ImageCapture imageCapture;
    ImageAnalysis myAnalyzer;


    CameraView cameraView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

//        cameraView = findViewById(R.id.view_finder);
//        cameraView.bindToLifecycle(this);
        viewFinder = findViewById(R.id.viewFinder); // 预览窗口
        recordButton = findViewById(R.id.recordButton);// 拍照按钮
        recordButton.setOnRecordListener(new RecordButton.OnRecordListener() {
            @Override
            public void onTackPicture() {
                takephoto();
            }

            @Override
            public void onRecordVideo() {

            }

            @Override
            public void onFinish() {

            }
        });
        if(allPermissionGranted()){
            startCamera(); // 开启相机
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},10);
        }

        cameraExecutor= Executors.newSingleThreadExecutor();
    }

    private boolean allPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera(){
        // 创建一个相机的管理器，相当于一个主管道。
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    //1 图像预览接口
                    preview = new Preview.Builder().build();
                    //2 图像分析接口
                    myAnalyzer = new ImageAnalysis.Builder().build();
                    myAnalyzer.setAnalyzer(cameraExecutor,
                            new QRcodeAnalyzer());
                    //3 拍照 接口
                    imageCapture = new ImageCapture.Builder().build();
                    //4 把我们需要的这三个接口安装到相机管理器的主线路上，实现截取数据的目的
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                    cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    camera = cameraProvider.bindToLifecycle(CameraXActivity.this, cameraSelector, preview,imageCapture,myAnalyzer);
                    //5 把相机的信息还得告诉预览窗口以做好准备显示数据
                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                } catch (ExecutionException e) {
                    Log.e(TAG, "run: binding lifecycle failed");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },ContextCompat.getMainExecutor(this));

    }

    private void takephoto() {
        //1 设置要保存的路径和文件名字
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) ,
                "image-"+new SimpleDateFormat(FILENAME_FORMATE, Locale.CHINA).format(System.currentTimeMillis())+".jpg");
        //2 定义 拍摄imageCapture实例
        ImageCapture.OutputFileOptions outputFileOptions=
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                //保存成功
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                //"保存失败"+
            }
        });
    }
}
