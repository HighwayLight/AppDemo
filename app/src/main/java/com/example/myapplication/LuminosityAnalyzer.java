package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

/**
 * Created by dushu on 2021/2/1 .
 */
public class LuminosityAnalyzer implements ImageAnalysis.Analyzer{


    @Override
    public void analyze(@NonNull ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] b=new byte[buffer.remaining()];
        buffer.get(b);
        // 按你的需要处理图片吧
        Log.e("TAG", "实时帧数据" );


        image.close();
    }
}
