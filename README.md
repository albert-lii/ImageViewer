# ImageViewer
图片查看器，仿微信朋友圈图片查看效果，支持图片手势缩放等操作，采用activity形式实现，只要一句代码即可调用。因为采用了属性动画，所以最低支持版本为11，即Android 3.0

## 联系方式
>电子邮箱：albertlii@163.com

## 演示
![demo](https://github.com/albert-lii/ImageViewer/blob/master/screenshot/demo.gif)

## 添加依赖
```java
Step 1:

   allprojects {
       repositories {
           ...
           maven { url 'https://jitpack.io' }
       }
    }
    
Step 2:

    dependencies {
        compile 'com.github.albert-lii:ImageViewer:1.0.6'
    }
```

## 使用方法
- Step 1:  
因为采用Activity方式实现，所以必须在AndroidManifest.Xml中注册`ImagePreviewActivity`
例如：  
```java
  <activity
      android:name="com.liyi.viewer.view.ImagePreviewActivity"
      android:launchMode="singleTask"
      android:theme="@android:style/Theme.Translucent.NoTitleBar.Fullscreen" />
```  
>注：一定要设置`Activity`的主题样式为透明`@android:style/Theme.Translucent`,至于是否去除标题栏或者是否全屏可按照需求设置  

- Step 2:
```java
imageViewer = ImageViewer.newInstance()
                .indexPos(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .imageData(mImageDatas);
                
                
 for (int j = 0; j < autoGridView.getChildCount(); j++) {
     int[] location = new int[2];
     // 获取在整个屏幕内的绝对坐标
     autoGridView.getChildAt(j).getLocationOnScreen(location);
     ViewData viewData = new ViewData();
     viewData.x = location[0];
     // 此处注意，如果`ImagePreviewActivity`使用全屏，而当前所在的Activity的状态栏独自占有高度，则还要减去状态栏的高度
     viewData.y = location[1];
     viewData.width = autoGridView.getChildAt(j).getMeasuredWidth();
     viewData.height = autoGridView.getChildAt(j).getMeasuredHeight();
     mViewDatas.add(viewData);
 }
 imageViewer.beginIndex(i)
    .viewData(mViewDatas)
    .show(PicActivity.this);
```
方法详解：
```java
  ImageViewer.newInstance()  
             // 点击的图片的序号（必填）
             .beginIndex(int index)
             // 点击的图片（非必填，一般不建议使用，主要是为了防止显示动画获取不到图像）
             .beginView(ImageView view)
             // 图片数据
             .imageData(ArrayList<Object> imageData)
             // ImageView在当前Activity中的位置信息和尺寸信息
             .viewData(ArrayList<ViewData> viewDatas)
             // 图片加载时的效果（采用的是glide4.1）
             .options(RequestOptions options)
             // 图片加载时是否显示进度条（默认显示）
             .showProgress(boolean isShow)
             // 设置进度条的样式
             .progressDrawableDrawable drawable()
             // 设置索引的显示位置
             .indexPos(int pos)
             // 实现图片
             .show(Context context);
```

## 赞赏  
如果你感觉 `ImageViewer` 帮助到了你，可以点右上角 "Star" 支持一下 谢谢！ ^_^

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
