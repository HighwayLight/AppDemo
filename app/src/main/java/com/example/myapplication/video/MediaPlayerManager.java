package com.example.myapplication.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.os.Environment.getExternalStorageState;

/**
 * Created by dushu on 2021/6/22 .
 */
public class MediaPlayerManager {

    private String mUserAgent = MediaPlayerManager.class.getSimpleName();

    private volatile HttpProxyCacheServer mProxyCacheServer = null;

    private final String DISK_CACHE_DIR_NAME = "Video";

    private static volatile MediaPlayerManager INSTANCES = null;

    public static MediaPlayerManager getInstance() {
        if (INSTANCES == null) {
            synchronized (MediaPlayerManager.class) {
                if (INSTANCES == null) {
                    INSTANCES = new MediaPlayerManager();
                }
            }
        }
        return INSTANCES;
    }

    public void init(Context context, String userAgent) {
        mProxyCacheServer = createProxyCacheServer(context);
        mUserAgent = Util.getUserAgent(context, userAgent);
    }

    /**
     * 将传入的uri构建为一个规媒体资源
     *
     * DashMediaSource         DASH.
     * SsMediaSource           SmoothStreaming.
     * HlsMediaSource          HLS.
     * ProgressiveMediaSource  常规媒体文件.
     *
     * @return 返回一个常规媒体资源
     */
    public MediaSource buildDataSource(Context context, String uri) {
        // 构建一个默认的Http数据资源处理工厂
        DataSource.Factory mHttpDataSourceFactory = new DefaultHttpDataSourceFactory(mUserAgent);
        // DefaultDataSourceFactory决定数据加载模式，是从网络加载还是本地缓存加载
        DataSource.Factory mDataSourceFactory = new DefaultDataSourceFactory(context, mHttpDataSourceFactory);
        // AndroidVideoCache库不支持DASH, SS(Smooth Streaming：平滑流媒体，如直播流), HLS数据格式，所以这里使用一个常见媒体转换数据资源工厂
        return new ProgressiveMediaSource.Factory(mDataSourceFactory).createMediaSource(Uri.parse(getProxyUrl(uri)));
    }

    /**
     * 获取代理地址
     */
    String getProxyUrl(String url){
        if (mProxyCacheServer != null) {
            return mProxyCacheServer.getProxyUrl(url);
        } else {
            Log.e("VideoActivity", " mProxyCacheServer = null");
        }
        return "";
    }

    /**
     * 是否缓存
     * @return true:已经缓存
     */


    /**
     * 创建视频加载代理
     */
    private HttpProxyCacheServer createProxyCacheServer(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .cacheDirectory(getDiskCacheDirectory(context)) // 设置磁盘存储地址
                .maxCacheSize(1024 * 1024 * 1024)     // 设置可存储1G资源
                .build();
    }

    /**
     * 视频磁盘缓存地址
     */
    @SuppressLint("SdCardPath")
    private File getDiskCacheDirectory(Context context) {
        File cacheDir = null;
        if (Environment.MEDIA_MOUNTED == getExternalStorageState()) {
            cacheDir = getExternalCacheDir(context);
        }
        if (cacheDir == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cacheDir = context.getDataDir();
            }
        }
        if (cacheDir == null) {
            String cacheDirPath = "/data/data/${context.packageName}/cache/";
            cacheDir = new File(cacheDirPath);
        }
        return new File(cacheDir, DISK_CACHE_DIR_NAME);
    }

    private File getExternalCacheDir(Context context) {
        File cacheDir = context.getExternalFilesDir("Cache");
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                return null;
            }
        }
        return cacheDir;
    }

    private String getExternalStorageState() throws NullPointerException {
        return Environment.getExternalStorageState();
    }

    /**
     * 删除所有视频缓存
     */
    public void deleteAllCache(Context context) throws IOException {
        File mFile = getDiskCacheDirectory(context);
        if (!mFile.exists()) {
            return;
        }
        File[] mFiles = mFile.listFiles();
        if (mFiles!= null && mFiles.length > 0) {
            for (File file: mFiles){
                deleteVideoCache(file);
            }
        }
    }

    private void deleteVideoCache(File file) throws IOException{
        if (file.isFile() && file.exists()){
            boolean isDeleted = file.delete();
            Log.e(mUserAgent, "删除视频缓存：${file.path}\t删除状态：$isDeleted");
        }
    }

    /**
     * 获取磁盘缓存的数据大小，单位：KB
     */
    public Long getDiskCacheSize(Context context) {
        File file = getDiskCacheDirectory(context);
        long blockSize = 0L;
        try {
            blockSize = file.isDirectory()? getFileSizes(file) : getFileSize(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockSize;
    }

    private long getFileSizes(File file) {
        long size = 0L;
        for (File f : file.listFiles()){
            if (f.isDirectory()) {
                size += getFileSizes(f);
            } else {
                try {
                    size += getFileSize(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    private long getFileSize(File file) {
        long size = 0L;
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                size = stream.available();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size;
    }


}
