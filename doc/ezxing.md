### EZXing


目前功能点 ：

    1. 扫一扫，高度灵敏功能。

    2. 生成二维码的工具类。ZxingUtils


使用方法，直接进行findviewbyid可以直接开始扫描，需要照相机权限，比较懒，就没写这个权限了。

写了一个ParsingCompleteListener 监听器

内部有两个方法，一个是会有识别type的，还有一个是直接把识别结果传回来的




        mScannerView = view.findViewById(R.id.findview);
        mScannerView.setOnParsingCompleteListener(new ParsingCompleteListener() {

            @Override
            public void onComplete(String text, String handingTime) {
                System.out.println(text);
                System.out.println(handingTime);
            }

            @Override
            public void onComplete(String text, String handingTime, DecodeType type) {
                switch (type) {
                    case EMAIL:
                        break;
                    case TEXT:
                        break;
                    case NUMBER:
                        break;
                    case URL:
                        break;
                }
            }
        });


当销毁的时候，需要调用release（）进行资源释放



        @Override
        public void onDestroy() {
            mScannerView.release();
            super.onDestroy();
        }


当扫描成功之后。界面就会停止扫描。如果需要恢复，可以调用reset方法进行重置


         // 扫描结束,重置扫描
         mScannerView.reset();


解释无力，还是看demo把，很简单

生成二维码的调用方法是ZXingUtils.encodeAsBitmap(当前的context, 需要生成的文字)。会返回一个bitmap。如果有rxjava，这步可以搞成异步


         mBitmap = ZXingUtils.encodeAsBitmap(getActivity(), text);


新增条形码扫描，原先不灵敏，只能横向扫描才能产生结果，目前做了优化，支持横向，纵向，斜度的暂时还不支持