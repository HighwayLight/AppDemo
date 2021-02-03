package com.example.myapplication;

import android.graphics.ImageFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by dushu on 2021/2/1 .
 * 二维码解析Analyzer
 */
public class QRcodeAnalyzer implements ImageAnalysis.Analyzer {

    private QRCodeReader reader = new QRCodeReader();
    private final Map<DecodeHintType, Object> hints1 = new Hashtable<>();

    public QRcodeAnalyzer(){
        hints1.put(DecodeHintType.CHARACTER_SET, "utf-8");
        hints1.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints1.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (ImageFormat.YUV_420_888 != image.getFormat()) {
            Log.e("QRcodeAnalyzer", "expect YUV_420_888, now = ${image.format}");
            return;
        }
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] b = new byte[buffer.remaining()];
        buffer.get(b);
        // 按你的需要处理图片吧
        Log.e("TAG", "实时帧数据");
        int height = image.getHeight();
        int width = image.getWidth();
        //TODO 调整crop的矩形区域，目前是全屏（全屏有更好的识别体验，但是在部分手机上可能OOM）
        LuminanceSource source = new PlanarYUVLuminanceSource(b, width, height, 0, 0, width, height, false);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        try {
            result = reader.decode(binaryBitmap, hints1);
            Log.e(TAG, "result = " + result.getText());

        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }


        image.close();
    }
}
