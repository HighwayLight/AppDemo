package com.example.myapplication;

import android.app.Application;

import com.example.myapplication.video.MediaPlayerManager;

/**
 * Created by dushu on 2021/6/22 .
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MediaPlayerManager.getInstance().init(this, "com.example.myapplication");
    }
}
