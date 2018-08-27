package com.mobgeek.adsdk.evideoview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

class EVideoView extends FrameLayout {


    /**
     * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
     */
    private             IMediaPlayer mMediaPlayer = null;
    public static final int          VIDEO_START  = 1;
    public static final int          VIDEO_PAUSE  = 2;
    /**
     * 视频文件地址
     */
    private             String       mPath        = "";
    private SurfaceView         surfaceView;
    private VideoPlayerListener listener;
    private Context             mContext;
    private boolean isInitMediaPlay = true;
    private Handler mHandler        = new Handler(Looper.getMainLooper());

    public EVideoView(@NonNull Context context) {
        super(context);
        initVideoView(context);
    }

    public EVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public EVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        mContext = context;
        //获取焦点
        setFocusable(true);
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        if (TextUtils.equals("", mPath)) {
            //如果是第一次播放视频，那就创建一个新的surfaceView
            mPath = path;
            createSurfaceView();
        } else {
            //否则就直接load
            mPath = path;
            release();
            load();
        }
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    public void setVideoPath(File path) {
        setVideoPath(path.getAbsolutePath());
    }

    /**
     * 新建一个surfaceview
     */
    private void createSurfaceView() {
        //生成一个新的surface view
        surfaceView = new SurfaceView(mContext);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT, Gravity.CENTER);
        surfaceView.setLayoutParams(layoutParams);
        // 如果有两个surface，则会冲突，需要设置永远置顶
        // surfaceView.setZOrderOnTop(true);
        this.addView(surfaceView);
    }

    public int getVideoWidth() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoWidth();
        }
        return 0;
    }

    public int getVideoHeight() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVideoHeight();
        }
        return 0;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    private void postProgress() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 如果停下，就不再更新进度
                if (isPlaying()) {
                    return;
                }
                if (mOnPlayStatusChangeListener != null) {
                    mOnPlayStatusChangeListener.onProgressChange(getCurrentPosition(), getDuration());
                }
                mHandler.postDelayed(this, 15);
            }
        }, 15);
    }


    /**
     * surfaceView的监听器
     */
    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // 其实这里只用对 MediaPlayer.setDisplay(surfaceView.getHolder());
            // 进行重新赋值就可以了，如果是重新创建，将会重新播放
            // 但是act被回收的时候，MediaPlayer可能会出现null,所以直接调用全部方法
            load();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // surfaceview创建成功后，加载视频
            start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 因为每次变小都会将surface缩小。所以需要进行暂停，当回来的时候，会重新调用load方法。
            pause();
        }
    }

    /**
     * 加载视频
     */
    private void load() {
        // 每次都要重新创建IMediaPlayer
        createPlayer();
        // 理论上说是不允许修改多次的路径
        String dataSource = mMediaPlayer.getDataSource();
        try {
            if (dataSource == null) {
                mMediaPlayer.setDataSource(mPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 给mediaPlayer设置视图
        mMediaPlayer.setDisplay(surfaceView.getHolder());
        if (isInitMediaPlay) {
            mMediaPlayer.prepareAsync();
            isInitMediaPlay = false;
        }
    }


    /**
     * 创建一个新的player，防止多次初始化，并且act被回收后，导致null问题.
     */
    private void createPlayer() {
        if (mMediaPlayer == null) {
            IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
            // ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
            // 开启硬解码
            // ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            mMediaPlayer = new IjkMediaPlayer();
            mMediaPlayer.setLooping(false);
            isInitMediaPlay = true;
            if (listener != null) {
                mMediaPlayer.setOnPreparedListener(listener);
                mMediaPlayer.setOnInfoListener(listener);
                mMediaPlayer.setOnSeekCompleteListener(listener);
                mMediaPlayer.setOnBufferingUpdateListener(listener);
                mMediaPlayer.setOnErrorListener(listener);
                mMediaPlayer.setOnVideoSizeChangedListener(listener);
                mMediaPlayer.setOnTimedTextListener(listener);
                mMediaPlayer.setOnCompletionListener(listener);
            }

        }
    }


    public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
    }

    /**
     * -------======--------- 下面封装了一下控制视频的方法
     */

    public void start() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            postProgress();
            if (mOnPlayStatusChangeListener != null) {
                mOnPlayStatusChangeListener.onStatusChange(VIDEO_START);
            }
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            if (mOnPlayStatusChangeListener != null) {
                mOnPlayStatusChangeListener.onStatusChange(VIDEO_PAUSE);
            }
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }


    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public void toggle() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                pause();
            } else {
                start();
            }
        }
    }


    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }


    public void seekTo(long l) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(l);
        }
    }

    private onPlayStatusChangeListener mOnPlayStatusChangeListener;

    public void setOnPlayStatusChangeListener(onPlayStatusChangeListener onPlayStatusChangeListener) {
        mOnPlayStatusChangeListener = onPlayStatusChangeListener;
    }

    public interface onPlayStatusChangeListener {
        void onStatusChange(int status);

        void onProgressChange(long progress, long max);
    }

}
