package com.mobgeek.adsdk.evideoview;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public interface VideoPlayerListener extends IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnTimedTextListener {


}
