# EView

--------------------------



## 封装了几个view

1. RecyclerView的自定义构建

    1). 功能一、 上拉和下拉

    2). 功能二、 可折叠

    <a href="/doc/erecycleview.md" >RecyclerView页面导航</a>

2. WebView

    1). 对webview进行封装.

    <a href="/doc/ewebview.md" >webview页面文档导航</a>

3. ijkplayer的集成以及界面绘制

    1). 对ijkplayer进行封装.

    2). 接口使用进行封装

    3). 因为奥运会将近？管理严，有很多直播源下架了，如果测试，可以修改直播源

    <a href="/doc/evideoview.md" >evideoview页面文档导航</a>

4. android google zxing 封装，高效

    1). 除去了很多没有必要的功能

    2). 留下了，生成二维码以及扫一扫的功能。

    3). 对条形码的数据做了处理，原先只能横向处理条形码。目前支持0度，90度，180度，270度,都可以扫描(微信也是这些角度，那我就这样算了吧)

    4). 新增解析图片。

    5). 响应时间都在1秒之内，速度还可以.

    <a href="/doc/ezxing.md" >ezxing页面文档导航</a>


## ps: 解释无力，还是看代码吧， 还有懒得写fragment了，直接看着用吧。

## ps：涉及到动态权限的，懒得写了，如果需要，可以去开一下