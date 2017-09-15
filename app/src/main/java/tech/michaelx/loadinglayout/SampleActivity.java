package tech.michaelx.loadinglayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.michaelx.loadinglibrary.LoadingLayout;

/**
 * 多状态加载布局演示demo
 * Created by michaelx on 2017/9/12.
 */
public class SampleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout mRefreshLayout;
    private LoadingLayout mLoadingLayout;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;

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
        mLoadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mRetryBtn = (Button) findViewById(R.id.retry_btn);
        mEmptyBtn = (Button) findViewById(R.id.empty_btn);
        mCustomBtn1 = (Button) findViewById(R.id.custom1);
        mCustomBtn2 = (Button) findViewById(R.id.custom2);

        mRetryBtn.setOnClickListener(this);
        mEmptyBtn.setOnClickListener(this);
        mCustomBtn1.setOnClickListener(this);
        mCustomBtn2.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
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

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
                Log.d("LOG", "data updated");
                mAdapter.update();
            }
        }, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_btn:
                startActivity(new Intent(this, EmptyActivity.class));
                break;

            case R.id.retry_btn:
                startActivity(new Intent(this, RetryActivity.class));
                break;

            case R.id.custom1:
                startActivity(new Intent(this, CustomActivity1.class));
                break;

            case R.id.custom2:
                startActivity(new Intent(this, CustomActivity2.class));
                break;

            default:
                break;
        }
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
            if (mData == null) {
                return;
            }
            int size = mData.size();
            for (int i = size; i < size + 10; i++) {
                mData.add("data===========" + i);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerVH(new TextView(SampleActivity.this));
        }

        @Override
        public void onBindViewHolder(RecyclerVH holder, int position) {
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
