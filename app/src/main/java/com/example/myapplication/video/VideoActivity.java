package com.example.myapplication.video;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.example.myapplication.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import static com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_SEEK;

/**
 * Created by dushu on 2021/4/22 .
 */
public class VideoActivity extends AppCompatActivity {

    private final String TAG = "VideoActivity";

    CustomVideoView videoView;

    private PlayerView playerView;
    private ImageView playStatusIv;

    private ExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;

    private AppCompatSeekBar mSeekBar;

//    private String src = "https://vd3.bdstatic.com/mda-mdmdxs4m9n1h2y1y/sc/cae_h264_clips/1619257447/mda-mdmdxs4m9n1h2y1y.mp4?auth_key=1619422392-0-0-71b49426e6e9b4fedaeb2ef1e64d8519&bcevod_channel=searchbox_feed&pd=1&pt=3&abtest=3000160_2";
    private String src = "https://bz10-static.oss-accelerate.aliyuncs.com/z/2021-06-10/51df3b0c-0f6e-41db-9bba-4c2b727ff88a.mp4";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

//        videoView = findViewById(R.id.video_view);
//        videoView.setVideoPath("https://vd3.bdstatic.com/mda-mdmdxs4m9n1h2y1y/sc/cae_h264_clips/1619257447/mda-mdmdxs4m9n1h2y1y.mp4?auth_key=1619422392-0-0-71b49426e6e9b4fedaeb2ef1e64d8519&bcevod_channel=searchbox_feed&pd=1&pt=3&abtest=3000160_2");
//        videoView.setVideoPath("/storage/emulated/0/Android/data/com.example.myapplication/files/Pictures/1620475422631.mp4");
        playStatusIv = findViewById(R.id.icon_play_status);
        playStatusIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "playStatusIv.onClick， playWhenReady = " + playWhenReady);
                if (player.isPlaying()) {
                    player.setPlayWhenReady(false);
                } else {
                    player.setPlayWhenReady(true);
                    Log.d(TAG, "player.setPlayWhenReady(true)");
                }
            }
        });
        playerView = findViewById(R.id.video_play_view);
        initializePlayer();


        PlayerControlView controller = playerView.findViewById(R.id.exo_controller);
        if (controller == null) {
            Log.e(TAG, "controller == null");
        } else {
            Log.e(TAG, "controller != null");
        }


        mSeekBar = controller.findViewById(R.id.exo_progress_placeholder);
        if (mSeekBar == null) {
            Log.e(TAG, "mSeekBar == null");
        } else {
            Log.e(TAG, "mSeekBar != null");
        }

    }
    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();

        playerView.setPlayer(player);
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                playStatusIv.setVisibility(visibility);
            }
        });

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

        //注册监听
        player.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int state) {
                Log.i(TAG, "onPlaybackStateChanged ( state = " + state + " )");
                switch (state) {
                    case Player.STATE_READY:
                        Log.d(TAG, "加载就绪，可以播放");
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "缓冲中...");
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "播放结束...");
                        break;
                    case Player.STATE_IDLE:
                        Log.d(TAG, "播放器没有可播放的媒体");
                        break;
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Log.e(TAG, "onPlayWhenReadyChanged ( playWhenReady = " + playWhenReady + ", reason = " + reason +" )");
                VideoActivity.this.playWhenReady = playWhenReady;
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.e(TAG, "onIsPlayingChanged ( isPlaying = " + isPlaying + " )");
                if (isPlaying) {
                    playStatusIv.setImageResource(R.mipmap.icon_pause);
                } else {
                    playStatusIv.setImageResource(R.mipmap.icon_start);
                }
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Log.e(TAG, "onPositionDiscontinuity ( oldPosition = " + oldPosition
                        + ", newPosition = " + newPosition
                        + ", reason = " + reason
                        +" )");
                if (reason == DISCONTINUITY_REASON_SEEK) {

                }
            }
        });
        @C.ContentType
        int mMediaSourceType = Util.inferContentType(Uri.parse(src), null);
        if(mMediaSourceType == C.TYPE_OTHER){
            // 获取构建后的媒体资源
            MediaSource mMediaSource = MediaPlayerManager.getInstance().buildDataSource(this, src);
            // 将媒体资源设置给播放器
            player.setMediaSource(mMediaSource);
            player.prepare();
        }
        // Build the media item.
//        MediaItem mediaItem = MediaItem.fromUri(src);
//        player.setMediaItem(mediaItem);
        //使用资源准备播放器
//        player.prepare();
        player.play();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }
}
