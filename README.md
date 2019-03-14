<h1>ImageViewer</h1>

![releasesvg] ![apisvg] [![license][licensesvg]][license]

<h2>关于</h2>

图片预览器，支持图片手势缩放、拖拽等操作，`自定义View`的模式显示，自定义图片加载方式，更加灵活，易于扩展，同时也适用于RecyclerView、ListView的横向和纵向列表模式，最低支持版本为Android 3.0及以上...  

<h2>功能</h2>

- 图片的基本缩放、滑动
- 微信朋友圈图片放大预览
- 微信朋友圈图片拖拽效果
- 今日头条图片拖拽效果
- 图片加载进度条

<h2>传送门</h2>

- [自定义属性](#1)
- [自定义方法](#2)
- [添加依赖](#3)
- [简单示例](#4)
- [超巨图加载解决方案](#5)

<h2>推荐</h2>

- [AutoGridView][AutoGridView] 宫格控件，QQ空间九宫格、普通宫格模式、点击添加照片...

<h2>项目演示</h2>

![demo-simple]  ![demo-custom]  
![demo-land]  ![demo-port]
  
<h2>apk体验</h2>

### [点我][demo-apk]

<h2 id="1">自定义属性</h2>  

| 属性名 | 描述 |    
| :---- | :---- |    
| ivr_show_index | 是否显示图片位置 |  
| ivr_do_enter | 是否开启进场动画 | 
| ivr_do_exit | 是否开启退场动画 |   
| ivr_duration | 进场与退场动画的执行时间 |    
| ivr_do_drag | 是否允许图片拖拽 |    
| ivr_drag_type | 拖拽模式（classic：今日头条效果 | wechat：微信朋友圈效果） |  
 
<h2 id="2">自定义方法</h2>    

| 方法名 | 描述 |
|:----|:----|
| setStartPosition(int position) | 设置开始展示的图片的位置 |
| setImageData(List list) | 设置图片资源 |
| setViewData(List<ViewData> list) | 设置目标 view 的相关数据 |
| setImageLoader(ImageLoader loader) | 设置图片加载类 |
| showIndex(boolean show) | 是否显示图片索引 |
| doDrag(boolean isDo) | 是否允许图片被拖拽 |
| setDragType(@ImageDraggerType int type) | 设置拖拽模式 |
| doEnterAnim(boolean isDo) | 是否开启进场动画 |
| doExitAnim(boolean isDo) | 是否开启退场动画 |
| setDuration(int duration) | 设置打开和关闭的动画执行时间 | 
| setOnImageChangedListener(OnImageChangedListener listener) | 设置图片切换监听 |
| setOnItemClickListener(OnItemClickListener listener) | 设置图片的单击击监听 |
| setOnItemLongClickListener(OnItemLongClickListener listener) | 设置图片的长按击监听 |
| setOnPreviewStatusListener(OnPreviewStatusListener listener) | 设置图片预览状态监听 |
| watch() | 开启图片预览 |
| close() | 关闭图片预览 |
| clear() | 清除所有数据 |<h1>ImageViewer</h1>

![releasesvg] ![apisvg] [![license][licensesvg]][license]

<h2>关于</h2>

图片浏览器，支持图片手势缩放、拖拽等操作，`自定义View`的模式显示，自定义图片加载方式，可自定义索引UI、ProgressView，更加灵活，易于扩展，同时也适用于RecyclerView、ListView的横向和纵向列表模式，最低支持版本为Android 3.0及以上...  

<h2>功能</h2>

- 图片的基本缩放、滑动
- 微信朋友圈图片放大预览
- 微信朋友圈图片拖拽效果
- 今日头条图片拖拽效果
- 自定义图片加加载
- 图片加载进度条
- 可自定义图片索引与图片加载进度UI

<h2>传送门</h2>

- [自定义属性](#1)
- [事件监听器](#2)
- [自定义UI](#3)
- [添加依赖](#4)
- [使用方法](#5)
- [超巨图加载解决方案](#6)

<h2>推荐</h2>

- [AutoGridView][AutoGridView] 宫格控件，QQ空间九宫格、普通宫格模式、点击添加照片...

<h2>项目演示</h2>

![demo-simple]  ![demo-custom]  
![demo-land]  ![demo-port]

<h2 id="1">自定义属性</h2>  

| 属性名 | 描述 |    
| :---- | :---- |    
| ivr_showIndex | 是否显示图片位置 |  
| ivr_playEnterAnim | 是否开启进场动画 | 
| ivr_playExitAnim | 是否开启退场动画 |   
| ivr_duration | 进场与退场动画的执行时间 |    
| ivr_draggable | 是否允许图片拖拽 |    
| ivr_dragMode | 拖拽模式（simple：今日头条效果 | agile：微信朋友圈效果） |  

<h2 id="2">事件监听器</h2>    

| 方法名 | 描述 |  
| :---- | :---- |    
| setOnItemClickListener(OnItemClickListener listener) | item 的单击事件 |
| setOnItemLongListener(OnItemLongPressListener listener) | item 的长按事件 |
| setOnItemChangedListener(OnItemChangedListener listener) | item 的切换事件 |
| setOnDragStatusListener(OnDragStatusListener listener) | 监听图片拖拽状态事件 |
| setOnBrowseStatusListener(OnBrowseStatusListener listener) | 监听图片浏览器状态事件 |
  
<h2 id="3">自定义UI</h2>  

- 自定义索引UI

框架中内置默认索引视图`DefaultIndexUI`，如要替换索引样式，可继承抽象类`IndexUI`,并在使用`watch(...)`方法前,调用下列方法加载自定义的indexUI

```java
loadIndexUI(@NonNull IndexUI indexUI)
```
- 自定义加载进度UI

框架中内置默认加载视图`DefaultProgressUI`，如要替换加载样式，可继承抽象类`ProgressUI`,并在使用`watch(...)`方法前,调用下列方法加载自定义的progressUI

```java
loadProgressUI(@NonNull ProgressUI progressUI)
```

<h2 id="4">添加依赖</h2> 

- Gradle
```Java
   Step 1:

   allprojects {
       repositories {
           ...
           // 如果添加依赖时，报找不到项目时（则项目正在审核），可以添加此句maven地址，如果找到项目，可不必添加
           maven { url "https://dl.bintray.com/albertlii/android-maven/" }
       }
    }
    
    
   Step 2:
   
   dependencies {
      compile 'indi.liyi.view:image-viewer:3.0.0-beta'
   }
```  

- Maven 
```Java
   <dependency>
      <groupId>indi.liyi.view</groupId>
      <artifactId>image-viewer</artifactId>
      <version>3.0.0-beta</version>
      <type>pom</type>
   </dependency>
```

<h2 id="5">使用方法</h2>

### XML 中添加 ImageViewer
```
  <indi.liyi.viewer.ImageViewer
        android:id="@+id/imageViewer"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

### 代码中设置 ImageViewer
一共提供两种配置ImageViewer的方法：

- 方法一：
```java
imageViewer.overlayStatusBar(false) // ImageViewer 是否会占据 StatusBar 的空间
           .imageData(list) // 图片数据
           .bindViewGroup(gridview) // 目标 viewGroup，例如类似朋友圈中的九宫格控件
           .imageLoader(new PhotoLoader()) // 设置图片加载方式
           .playEnterAnim(true) // 是否开启进场动画，默认为true
           .playExitAnim(true) // 是否开启退场动画，默认为true
           .duration(true) // 设置进退场动画时间，默认300
           .showIndex(true) // 是否显示图片索引，默认为true
           .loadIndexUI(indexUI) // 自定义索引样式，内置默认样式
           .loadProgressUI(progressUI) // 自定义图片加载进度样式，内置默认样式
           .watch(position); // 开启浏览
```
此方法是用imageData()配合bindViewGroup()方法，来在内部构建自动构建item的信息模型ViewData，适用于目标ViewGroup类似于朋友圈九宫格控件这类场景，目标ViewGroup如果是ListView这种可重复利用item的控件，则不可用。

- 方法二：
```Java
   imageViewer.overlayStatusBar(false) // ImageViewer 是否会占据 StatusBar 的空间
              .viewData(vdList) // 数据源
              .imageLoader(new PhotoLoader()) // 设置图片加载方式
              .playEnterAnim(true) // 是否开启进场动画，默认为true
              .playExitAnim(true) // 是否开启退场动画，默认为true
              .duration(true) // 设置进退场动画时间，默认300
              .showIndex(true) // 是否显示图片索引，默认为true
              .loadIndexUI(indexUI) // 自定义索引样式，内置默认样式
              .loadProgressUI(progressUI) // 自定义图片加载进度样式，内置默认样式
              .watch(position);
```
此方法直接使用viewData()设置框架所需要的数据源

<h2 id="6">超巨图解决方案</h2>

1. 因为可以自定义图片加载方法，在加载图片前可以先压缩图片
2. 项目内部目前使用的图片缩放控件为PhotoView，可以将PhotoView用以下控件代替：

   - 使用 [SubsamplingScaleImageView](SubsamplingScaleImageView) 代替 PhotoView（推荐）
   - 或者使用 [BigImageView](BigImageView) 代替 PhotoView

<h2>赞赏</h2>

如果你感觉 `ImageViewer` 帮助到了你，可以点右上角 "Star" 支持一下哦！:blush:

<h2>LICENSE</h2>

Copyright 2017 liyi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



[releasesvg]: https://img.shields.io/badge/version-3.0.0-brightgreen.svg
[apisvg]: https://img.shields.io/badge/sdk-14+-brightgreen.svg
[licensesvg]: https://img.shields.io/badge/license-Apache--2.0-blue.svg
[license]:http://www.apache.org/licenses/LICENSE-2.0

[AutoGridView]:https://github.com/albert-lii/AutoGridView
[demo-simple]:https://github.com/albert-lii/ImageViewer/blob/new/snapshot/demo_simple.gif
[demo-custom]:https://github.com/albert-lii/ImageViewer/blob/new/snapshot/demo_custom.gif
[demo-land]:https://github.com/albert-lii/ImageViewer/blob/new/snapshot/demo_land.gif
[demo-port]:https://github.com/albert-lii/ImageViewer/blob/new/snapshot/demo_port.gif
[demo-apk]:https://github.com/albert-lii/ImageViewer/blob/new/apk/release/app-release.apk

[SubsamplingScaleImageView]:https://github.com/davemorrissey/subsampling-scale-image-view
[BigImageView]:https://github.com/Piasy/BigImageViewer


