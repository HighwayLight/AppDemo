package com.example.myapplication.video;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.myapplication.R;

import java.io.IOException;

/**
 * Created by dushu on 2021/4/22 .
 */
public class CustomVideoView extends ConstraintLayout implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, View.OnClickListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnBufferingUpdateListener,
        AppCompatSeekBar.OnSeekBarChangeListener {


    private static final String TAG = "VideoView";
    private LinearLayout controlLl;

    private ImageView playOrPauseIv;
    private ImageView playStatusIv;
    private SurfaceView videoSuf;
    private MediaPlayer mPlayer;
    private SeekBar mSeekBar;
    private String path;

    private TextView playingTimeTv, endTimeTv;

    private int mVideoWidth,mVideoHeight;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TIME:
                    updateTime();
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 500);
                    break;
                case HIDE_CONTROL:
                    hideControl();
                    break;
            }
        }
    };

    private boolean isShow = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private boolean mIsVideoSizeKnown = false;

    public static final int UPDATE_TIME = 0x0001;
    public static final int HIDE_CONTROL = 0x0002;

    public CustomVideoView(Context context) {
        this(context, null);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.video_view, this);
        initView();
        initData();
        initSurfaceView();
        initPlayer();
        initEvent();
    }

    private void initData() {
//        path = "https://vd3.bdstatic.com/mda-mdmdxs4m9n1h2y1y/sc/cae_h264_clips/1619257447/mda-mdmdxs4m9n1h2y1y.mp4?auth_key=1619422392-0-0-71b49426e6e9b4fedaeb2ef1e64d8519&bcevod_channel=searchbox_feed&pd=1&pt=3&abtest=3000160_2";
    }


    //初始化
    private void initView() {
        controlLl = findViewById(R.id.control_layout);
        playOrPauseIv = findViewById(R.id.iv_play_or_pause);
        playStatusIv = findViewById(R.id.iv_status_play);
        mSeekBar = findViewById(R.id.seekBar);
        playingTimeTv = findViewById(R.id.tv_playing_time);
        endTimeTv = findViewById(R.id.tv_duration_time);

        videoSuf = findViewById(R.id.surfaceView);
    }

    private void initSurfaceView() {
        videoSuf = (SurfaceView) findViewById(R.id.surfaceView);
        videoSuf.setZOrderOnTop(false);
        videoSuf.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoSuf.getHolder().addCallback(this);
        videoSuf.setOnClickListener(this);
    }

    private void initPlayer(){
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setOnVideoSizeChangedListener(this);

        try {
            if (!TextUtils.isEmpty(path)) {
                mPlayer.setDataSource(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        playOrPauseIv.setOnClickListener(this);
        playStatusIv.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(this);

        findViewById(R.id.root_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showControl();
            }
        });
//        this.getRootView().setOnClickListener(this);
        videoSuf.setOnClickListener(this);
    }

    public void setVideoPath(@NonNull String path){
        this.path = path;
        try {
            mPlayer.setDataSource(this.path);
//            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //网络流媒体播放结束监听
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion()");
    }

    //设置错误信息监听
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError()" + " what = " + what + " extra = " + extra);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onInfo()" + " what = " + what + " extra = " + extra);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playingTimeTv.setText(FormatTimeUtil.formatLongToTimeStr(mp.getCurrentPosition()));
        endTimeTv.setText(FormatTimeUtil.formatLongToTimeStr(mp.getDuration()));
        mSeekBar.setMax(mp.getDuration());
        mSeekBar.setProgress(mp.getCurrentPosition());

        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
        Log.e(TAG, "onPrepared()");
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.e(TAG, "onSeekComplete()");
    }

    //视频尺寸监听
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.e(TAG, "onVideoSizeChanged" + width + ") or height(" + height + ")");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");

            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mPlayer.setDisplay(holder);
        if (!TextUtils.isEmpty(path)) {
//            mPlayer.prepareAsync();
        }
        Log.e(TAG, "surfaceCreated()" );

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged()" );
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed()" );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_or_pause:
            case R.id.iv_status_play:
                play();
                break;
//            case R.id.root_layout:
            case R.id.surfaceView:
                showControl();
                break;
        }

    }


    //OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mPlayer != null && fromUser){
            mPlayer.seekTo(progress);
            Log.d(TAG, "onProgressChanged()"  + " progress = " + progress + " fromUser = " + fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e(TAG, "onStartTrackingTouch()" );

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.e(TAG, "onStopTrackingTouch()" );
    }


    /**
     * 开始播放
     */
    private void play(){
        if (mPlayer == null || TextUtils.isEmpty(path)) {
            return;
        }

        if (!mIsVideoReadyToBePlayed) {
            try {
                mPlayer.prepare();
                mIsVideoReadyToBePlayed = true;
                Log.e(TAG, "prepare()");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Log.e(TAG,"playPath = " + path);
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mHandler.removeMessages(UPDATE_TIME);
            mHandler.removeMessages(HIDE_CONTROL);
            playOrPauseIv.setVisibility(View.VISIBLE);
            playOrPauseIv.setImageResource(R.mipmap.icon_start);

            playStatusIv.setImageResource(R.mipmap.status_pause);
        } else {
            mPlayer.start();
            mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 500);
            mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 5000);
            playOrPauseIv.setVisibility(View.INVISIBLE);
            playOrPauseIv.setImageResource(R.mipmap.icon_pause);

            playStatusIv.setImageResource(R.mipmap.status_playing);
        }
    }

    public void stop(){
        if (mPlayer == null || TextUtils.isEmpty(path)) {
            return;
        }
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }


    /**
     * 更新播放时间
     */
    private void updateTime() {
        playingTimeTv.setText(FormatTimeUtil.formatLongToTimeStr(
                mPlayer.getCurrentPosition()));
        mSeekBar.setProgress(mPlayer.getCurrentPosition());
    }

    /**
     * 隐藏进度条
     */
    private void hideControl() {
        isShow = false;
        mHandler.removeMessages(UPDATE_TIME);
        controlLl.animate().setDuration(300).translationY(controlLl.getHeight());
        controlLl.setVisibility(INVISIBLE);
        playOrPauseIv.animate().setDuration(300).alpha(1);
        playOrPauseIv.setVisibility(INVISIBLE);
    }

    /**
     * 显示进度条
     */
    private void showControl() {
//        if (isShow) {
//            play();
//        }
        isShow = true;
        mHandler.removeMessages(HIDE_CONTROL);
        mHandler.sendEmptyMessage(UPDATE_TIME);
        mHandler.sendEmptyMessageDelayed(HIDE_CONTROL, 5000);
        controlLl.setVisibility(VISIBLE);
        controlLl.animate().setDuration(300).translationY(0);
        playOrPauseIv.setVisibility(VISIBLE);
        playOrPauseIv.animate().setDuration(300).alpha(0);
    }


    private void startVideoPlayback() {
        videoSuf.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
//        mPlayer.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e(TAG, "onBufferingUpdate()" + " percent = " + percent);
    }
}
