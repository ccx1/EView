### EVideoView


封装了ijkplayer

        mEVideoView = new EVideoView(mContext);
        mEVideoView.setListener(this);
        mEVideoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoPlayView.isComplete) {
                    // 优化 todo
                    mEVideoView.toggle();
                }
            }
        });
        mEVideoView.setVideoPath(mPath);


toggle()。是对按钮键切换的封装，也就暂停和播放的封装

目前使用，则是进行初始化之后会自动播放。
如果不需要自动播放，可以将SurfaceCallback修改即可

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
                //surfaceview创建成功后，加载视频
                start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // 因为每次变小都会将surface缩小。所以需要进行暂停，当回来的时候，会重新调用load方法。
                pause();
            }
        }


支持本地视频和网络视频.支持直播流，支持rtmp格式的流媒体协议
目前没有做特别的界面优化，需要自定义ui。这里只是做了ijkplayer的使用封装。界面ui还需开发者自定义


