# LoadingLayout
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[ ![Download](https://api.bintray.com/packages/xiong-it/AndroidRepo/LoadingLayout/images/download.svg) ](https://bintray.com/xiong-it/AndroidRepo/LoadingLayout/_latestVersion)  
应用于Android中的一个加载数据不同状态的类库（自定义控件）。 

# Compile
打开你的app module中的build.gradle,添加依赖：
```groovy  
compile 'tech.michaelx.loadinglibrary:loadinglibrary:1.0.1'
```  

# Sample  
在layout的xml中使用如下：
```xml  
<?xml version="1.0" encoding="utf-8"?>
<tech.michaelx.loadinglibrary.LoadingLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/loading_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:contentDescription="loadingBackground可以是drawable或者颜色"
    android:padding="10dp"
    app:emptyView="@layout/empty_layout"
    app:errorView="@layout/failure_layout"
    app:loadingAnimator="@animator/loading"
    app:loadingBackground="#1296db"
    app:loadingView="@layout/loading_layout"
    app:retryLoadAlways="true"
    app:showLoadingDebug="true">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="本页面使用属性自定义loading各种状态" />

</tech.michaelx.loadinglibrary.LoadingLayout>
```
  
**常用自定义属性**  

`app:loadingView="@layout/loading_layout"` :指定加载时的展示布局，可以为空，为空时使用系统默认ProgressBar  
`app:loadingAnimator="@animator/loading"`:只用上述属性制定时，该属性方可生效，作用于加载布局的动画  
`app:loadingBackground="#1296db"`:指定加载过程中页面背景，可以是颜色或者drawable，默认为adnroid:color/white  
`app:emptyView="@layout/empty_layout"`:指定了数据为空时的展示布局，可以为空，有默认布局，详细可看下方演示gif图   
`app:errorView="@layout/failure_layout"`:指定加载失败时的展示布局，可以为空，有默认布局，详细可看下方演示gif图  
`app:retryLoadAlways="true"`:是否开启数据为空时点击重试，默认为false  
`app:showLoadingDebug="true"`:是否开启布局预览调试，默认为false，开启后可以在AS中正常预览布局文件，**打包时请一定写false！**  
<br>
其他更多属性请看  
```xml  
<resources>
    <declare-styleable name="LoadingLayout">
        <!--设置数据为空的layout-->  
        <attr name="emptyView" format="reference" />
        <!--设置加载失败的layout-->  
        <attr name="errorView" format="reference" />
        <!--设置加载中的layout-->  
        <attr name="loadingView" format="reference" />
        <!--设置加载动画id-->  
        <attr name="loadingAnimator" format="reference" />
        <!--设置加载中的背景，或者颜色-->  
        <attr name="loadingBackground" format="reference|color" />
        <!--设置默认Progressbar的progress_drawable-->  
        <attr name="loadingProgressDrawable" format="reference" />
        <!--设置数据为空时的图片-->  
        <attr name="emptyDrawable" format="reference" />
        <!--设置数据为空时的提示语-->  
        <attr name="emptyText" format="string" />
        <!--设置加载失败时的图片-->  
        <attr name="errorDrawable" format="reference" />
        <!--设置加载失败时的提示语-->  
        <attr name="errorText" format="string" />
        <!--设置是否总是点击重试，无论数据为空或者失败，默认false-->  
        <attr name="retryLoadAlways" format="boolean" />
        <!--设置自动显示加载调试-->  
        <attr name="showLoadingDebug" format="boolean" />
    </declare-styleable>
</resources>  
```  
  
**Activity/Fragment代码中**  

```java
// 初始化布局对象  
mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);  
  
// 加载完毕/加载成功  
mLoadingLayout.loadComplete();  
  
// 数据为空  
mLoadingLayout.showEmpty();  
  
// 加载失败  
mLoadingLayout.loadFailure();  
  
// 设置点击重试监听  
mLoadingLayout.setOnRetryLoadListener(OnRetryLoadListener);  

// 显示加载中  
// 代码调用了setLoadingView()，才需要手动调用showLoading()
mLoadingLayout.showLoading();
```
其他公共方法还提供了更多功能，具体可参看app-demo源码。  

# 效果图
加载中，加载完成，数据为空，加载失败，点击重试 几种状态演示gif   
![加载几种状态演示](https://raw.githubusercontent.com/xiong-it/LoadingLayout/master/output/LoadingLayout.gif) 
  
# 注意事项  
LoadingLayout需要依赖于`appcompat-v7`，请在工程中添加这两个依赖（版本可自定义，不建议低于25.3.1）：  
```groove
compile "com.android.support:appcompat-v7:25.3.1"  
```
  
# changelog
v1.0.1  
添加一个自定义属性方便xml中预览布局  
解决一个可能的空指针异常 
优化demo  
  
v1.0.0  
实现加载中，加载成功，数据获取为空，加载失败4种状态基本功能  
实现演示demo
