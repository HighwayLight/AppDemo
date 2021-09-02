package com.example.myapplication.gif;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifOptions;
import pl.droidsonroids.gif.InputSource;

/**
 * Created by dushu on 2021/8/28 .
 */
public class CustomGifDrawable extends GifDrawable {

    GifActivity gifActivity;

    public CustomGifDrawable(@NonNull String filePath) throws IOException {
        super(filePath);
    }

    public CustomGifDrawable(@NonNull File file) throws IOException {
        super(file);
    }

    public CustomGifDrawable(@NonNull InputStream stream) throws IOException {
        super(stream);
    }

    public CustomGifDrawable(@NonNull AssetFileDescriptor afd) throws IOException {
        super(afd);
    }

    public CustomGifDrawable(@NonNull FileDescriptor fd) throws IOException {
        super(fd);
    }

    public CustomGifDrawable(@NonNull byte[] bytes) throws IOException {
        super(bytes);
    }

    public CustomGifDrawable(@NonNull ByteBuffer buffer) throws IOException {
        super(buffer);
    }

    public CustomGifDrawable(@Nullable ContentResolver resolver, @NonNull Uri uri) throws IOException {
        super(resolver, uri);
    }

    protected CustomGifDrawable(@NonNull InputSource inputSource, @Nullable GifDrawable oldDrawable, @Nullable ScheduledThreadPoolExecutor executor, boolean isRenderingTriggeredOnDraw, @NonNull GifOptions options) throws IOException {
        super(inputSource, oldDrawable, executor, isRenderingTriggeredOnDraw, options);
    }

    public CustomGifDrawable(String path, GifActivity gifActivity) throws IOException {
        this(path);
        this.gifActivity = gifActivity;
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        boolean change = super.setVisible(visible, restart);
        Log.e("Gif", "setVisible change = " + change + ", visible = " + visible);
        Log.e("Gif", "setVisible gifActivity.activityStatus = " + gifActivity.activityStatus);
        return change;
    }
}
