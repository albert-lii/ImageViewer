# ImageViewer

![releasesvg] ![apisvg] [![license][licensesvg]][license]

## 关于
图片查看器，仿微信朋友圈图片查看效果，支持图片手势缩放、拖拽等操作，全新2.0版本，由1.0版本的`Activity`模式实现改为`自定义View`的模式显示，自定义图片加载方式，更加灵活，易于扩展，同时也适用于RecyclerView、ListView的横向和纵向列表模式，最低支持版本为Android 3.0及以上...  

## 推荐
- [AutoGridView][AutoGridView] 宫格控件，QQ空间九宫格、普通宫格模式、点击添加照片...

## 演示
![demo-simple]  ![demo-custom]  
![demo-land]  ![demo-port]
  
## apk体验
### [点我][demo-apk]
  
## 添加依赖
* Gradle
```Java
   Step 1:

   allprojects {
       repositories {
           ...
           // 此句maven是为因为项目中使用了jitpack上的开源项目PhotoView
           maven { url 'https://jitpack.io' }
           // 如果添加依赖时，报找不到项目时（项目正在审核），可以添加此句maven地址，如果找到项目，可不必添加
           maven { url "https://dl.bintray.com/albertlii/android-maven/" }
       }
    }
    
    
   Step 2:
   dependencies {
       compile 'com.liyi.view:image-viewer:2.0.2'
   }
```  

* Maven 
```Java
   <dependency>
      <groupId>com.liyi.view</groupId>
      <artifactId>image-viewer</artifactId>
      <version>2.0.1</version>
      <type>pom</type>
   </dependency>
```

## 自定义属性方法  
| 属性名 | 描述 |  
| :---- | :---- |  
| ivr_show_index | 是否显示图片序号 |
| ivr_drag_enable | 是否允许图片拖拽 |
| ivr_enter_anim | 是否开启进入动画 |
| ivr_exit_anim | 是否开启退出动画 |
| ivr_anim_duration | 进入与退出动画的执行时间 |
  

| 方法名 | 描述 |
|:----|:----|
| void setImageBackground(Drawable drawable) | 设置图片背景 |
| void setImageBackgroundResource(@DrawableRes int resid) | 设置图片背景 |
| void setImageBackgroundColor(@ColorInt int color) | 设置图片背景 |
| void setStartPosition(int position) | 设置开始展示的图片的位置 |
| void setImageData(List<Object> list) | 设置图片资源 |
| void setViewData(List<ViewData> list) | 设置 View 数据（尺寸、坐标等信息） |
| void setImageLoader(ImageLoader loader) | 设置图片加载类 |
| void setOnImageChangedListener(OnImageChangedListener listener) | 设置图片切换监听 |
| void setOnViewClickListener(OnViewClickListener listener) | 设置图片的点击监听 |
| void setOnWatchStatusListener(OnWatchStatusListener listener) | 设置图片浏览状态监听 |
| void showIndex(boolean show) | 是否显示图片序号 |
| void doDragAction(boolean isDo) | 是否允许图片被拖拽 |
| void doEnterAnim(boolean isDo) | 是否开启图片浏览启动动画 |
| void doExitAnim(boolean isDo) | 是否开启图片浏览退出动画 |
| void setAnimDuration(int duration) | 设置打开和关闭的动画执行时间 |  
| void excuteEnterAnim() | 执行开始动画 |
| void excuteExitAnim() | 执行结束动画 |
| void watch() | 开启图片浏览 |
| void close() | 关闭图片浏览 |
| void clear() | 清除所有数据 |
| void setImageZoomable(boolean zoomable) | 设置图片是否可缩放 |  
| boolean isImageZoomable() | 获取图片是否可缩放 |  
| float getImageScale() | 获取图片当前的缩放级别 |  
| int getCurrentPosition() | 获取当前图片的位置 |  
| View getCurrentView() | 获取当前 Item 的视图 |

## 使用方法
#### XML
```
  <com.liyi.viewer.widget.ImageViewer
        android:id="@+id/imagePreivew"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

#### 代码中
```Java
  // 图片浏览的起始位置
  imageViewer.setStartPosition(position);
  // 图片的数据源
  imageViewer.setImageData(mImageList);
  // 外部 View 的位置以及尺寸等信息
  imageViewer.setViewData(mViewDatas);
  // 自定义图片的加载方式
  imageViewer.setImageLoader(new ImageLoader() {
        @Override
        public void displayImage(final int position, Object src, final ImageView view) {
               Glide.with(SimplePreviewActivity.this)
                    .load(src)
                    .into(new SimpleTarget<Drawable>() {
                          @Override
                          public void onLoadStarted(@Nullable Drawable placeholder) {
                                 super.onLoadStarted(placeholder);
                                 view.setImageDrawable(placeholder);
                          }

                          @Override
                          public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                 super.onLoadFailed(errorDrawable);
                                 view.setImageDrawable(errorDrawable);
                          }

                          @Override
                          public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                 view.setImageDrawable(resource);
                                 mImageList.set(position, resource);
                                 mViewDatas.get(position).setImageWidth(resource.getIntrinsicWidth());
                                 mViewDatas.get(position).setImageHeight(resource.getIntrinsicHeight());
                          }
                     });
   }});
   // 开启图片浏览
   imageViewer.watch();
```

## 赞赏  
如果你感觉 `ImageViewer` 帮助到了你，可以点右上角 "Star" 支持一下 谢谢！:blush:

## LICENSE
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



[releasesvg]: https://img.shields.io/badge/version-2.0.2-brightgreen.svg
[apisvg]: https://img.shields.io/badge/sdk-14+-brightgreen.svg
[licensesvg]: https://img.shields.io/badge/license-Apache--2.0-blue.svg
[license]:http://www.apache.org/licenses/LICENSE-2.0

[AutoGridView]:https://github.com/albert-lii/AutoGridView
[demo-simple]:https://github.com/albert-lii/ImageViewer/blob/master/snapshot/demo_simple.gif
[demo-custom]:https://github.com/albert-lii/ImageViewer/blob/master/snapshot/demo_custom.gif
[demo-custom]:https://github.com/albert-lii/ImageViewer/blob/master/snapshot/demo_land.gif
[demo-custom]:https://github.com/albert-lii/ImageViewer/blob/master/snapshot/demo_port.gif
[demo-apk]:https://github.com/albert-lii/ImageViewer/blob/master/apk/release/app-release.apk



