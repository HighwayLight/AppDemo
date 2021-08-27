package com.example.myapplication;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dushu on 2021/1/5 .
 */
public class ImageLoader extends LoaderM implements LoaderManager.LoaderCallbacks {

    private String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};


    private String sortOrder;
    private String albumId;
    private int type;

    private Context mContext;
    private DataCallback mLoader;


    public ImageLoader(Context context, String albumId, int type, int pageIndex, int pageSize, DataCallback loader) {
        this.mContext = context;
        this.mLoader = loader;

        this.sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC limit " + pageSize + " offset " + pageIndex * pageSize;
        Log.e("ZKMediaLibrary", "sortOrder = " + this.sortOrder);
        sortOrder = null;
        this.albumId = albumId;
        this.type = type;

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String selection = null;
        if (!TextUtils.isEmpty(albumId) && !TextUtils.equals( albumId,"all")) {
            selection = MediaStore.Images.ImageColumns.BUCKET_ID + " = " + albumId;
        }
        Log.e("ZKMediaLibrary", "selection = " + selection);
        CursorLoader cursorLoader = new CursorLoader(
                mContext,
                queryUri,
                IMAGE_PROJECTION,
                selection,
                null, // Selection args (none).
                sortOrder
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object o) {
        ArrayList<MediaEntity> medias = new ArrayList<>();
        Cursor cursor = (Cursor) o;
        Log.e("ZKMediaLibrary","ImageLoader.onLoadFinished()");
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

            if (size < 1 || !new File(path).exists()) continue;

            String dirName = getParent(path);
            MediaEntity media = new MediaEntity(path, mediaType, size, id, dirName);
            Log.e("ZKMediaLibrary","media.URI = " + media.URI);
            medias.add(media);
        }

        mLoader.onData(medias);
        if(Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
//        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
