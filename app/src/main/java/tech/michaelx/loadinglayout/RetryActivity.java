package tech.michaelx.loadinglayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.michaelx.loadinglibrary.LoadingLayout;

/**
 * Created by michaelx on 2017/9/13.
 */

public class RetryActivity extends AppCompatActivity implements View.OnClickListener, LoadingLayout.OnRetryLoadListener {
    private SwipeRefreshLayout mRefreshLayout;
    private LoadingLayout mLoadingLayout;
    private RecyclerView mRecyclerView;
    private RetryActivity.RecyclerAdapter mAdapter;

    private Button mRetryBtn, mEmptyBtn, mCustomBtn1, mCustomBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        initData();
        initView();

    }

    private void initView() {
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mRefreshLayout.setEnabled(false);
        mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RetryActivity.RecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);


        mEmptyBtn = (Button) findViewById(R.id.empty_btn);
        mRetryBtn = (Button) findViewById(R.id.retry_btn);
        mCustomBtn1 = (Button) findViewById(R.id.custom1);
        mCustomBtn2 = (Button) findViewById(R.id.custom2);
        mRetryBtn.setVisibility(View.GONE);
        mCustomBtn1.setVisibility(View.GONE);
        mCustomBtn2.setVisibility(View.GONE);
        mEmptyBtn.setOnClickListener(this);

        mLoadingLayout.setOnRetryLoadListener(this);
    }


    private void initData() {
        // data is simulated by delaying 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingLayout.loadFailure();

                mAdapter.initData();
                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        // 跳转另一个空数据Activity
        startActivity(new Intent(this, EmptyActivity.class));
    }

    @Override
    public void onReLoad() {
        // data is simulated by delaying 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoadingLayout.loadComplete();

                mAdapter.initData();
                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }


    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerVH> {
        private List<String> mData;

        public RecyclerAdapter() {
        }

        public void initData() {
            mData = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                mData.add("data===========" + i);
            }
        }

        public void update() {
            for (int i = 10; i < 20; i++) {
                mData.add("data===========" + i);
            }
        }

        @Override
        public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerVH(new TextView(RetryActivity.this));
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.RecyclerVH holder, int position) {
            holder.mItemView.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        class RecyclerVH extends RecyclerView.ViewHolder {
            public TextView mItemView;

            public RecyclerVH(View itemView) {
                super(itemView);
                mItemView = (TextView) itemView;
            }
        }
    }
}
