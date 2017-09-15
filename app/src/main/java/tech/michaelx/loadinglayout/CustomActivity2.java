package tech.michaelx.loadinglayout;

import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tech.michaelx.loadinglibrary.LoadingLayout;

/**
 * Created by MichaelX on 2017/9/14.
 * 使用代码自定义各种状态显示
 */

public class CustomActivity2 extends AppCompatActivity implements LoadingLayout.OnRetryLoadListener {
    private LoadingLayout mLoadingLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom2);

        mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);

        mLoadingLayout.setLoadingView(getLayoutInflater().inflate(R.layout.loading_layout, null));
//        mLoadingLayout.setLoadingView(R.layout.loading_layout);
        mLoadingLayout.setLoadingAnim(AnimatorInflater.loadAnimator(this, R.animator.loading));
//        mLoadingLayout.setLoadingAnim(R.animator.loading);

        mLoadingLayout.setErrorDrawable(R.drawable.failure);
//        mLoadingLayout.setErrorDrawable(getResources().getDrawable(R.drawable.failure));
        mLoadingLayout.setErrorText(R.string.failure_text);
//        mLoadingLayout.setErrorText(getResources().getText(R.string.failure_text).toString());

//        mLoadingLayout.setEmptyDrawable(R.drawable.icon_empty);
        mLoadingLayout.setEmptyDrawable(getResources().getDrawable(R.drawable.icon_empty));
//        mLoadingLayout.setErrorText(R.string.empty_text);
        mLoadingLayout.setEmptyText(getResources().getText(R.string.empty_text).toString());

        // 如果调用了setLoadingView()，需要手动调用showLoading()
        mLoadingLayout.showLoading();

        mLoadingLayout.setOnRetryLoadListener(this);

        // 延时3s模拟数据获取
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingLayout.loadFailure();
            }
        }, 3000);
    }

    @Override
    public void onReLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingLayout.loadComplete();
//                mLoadingLayout.showEmpty();
            }
        }, 3000);
    }

}
