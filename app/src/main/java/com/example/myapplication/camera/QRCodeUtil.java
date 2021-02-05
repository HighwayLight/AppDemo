package com.example.myapplication.camera;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import static android.content.ContentValues.TAG;

/**
 * Created by dushu on 2021/2/3 .
 * 二维码解析工具类
 */
public class QRCodeUtil {

    public static Result parseQrCode(Bitmap bitmap) {
        Map<DecodeHintType, Object> hints1 = new Hashtable<>();
        hints1.put(DecodeHintType.CHARACTER_SET, "utf-8");
//        hints1.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
//        hints1.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        hints1.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        //从一个ARGB的像素数组转换成一个RGB数据
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result;
        try {
            result = reader.decode(bitmap1, hints1);
            Log.e(TAG, "result = " + result.getText());
            return result;
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            Log.e(TAG, "二维码解析异常" + e.toString());
        }
        return null;
    }

}
