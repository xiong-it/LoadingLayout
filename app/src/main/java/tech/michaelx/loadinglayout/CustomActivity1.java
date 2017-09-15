package tech.michaelx.loadinglayout;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import tech.michaelx.loadinglibrary.LoadingLayout;

/**
 * Created by michaelx on 2017/9/14.
 */

public class CustomActivity1 extends AppCompatActivity implements LoadingLayout.OnRetryLoadListener {

    private LoadingLayout mLoadingLayout;
    private int mRetryCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom1);

        mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);

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
        mRetryCount++;
        // 延时3s模拟数据获取
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRetryCount == 2) {
                    mLoadingLayout.loadComplete();
                } else {
                    mLoadingLayout.showEmpty();
                }

            }
        }, 3000);
    }
}
