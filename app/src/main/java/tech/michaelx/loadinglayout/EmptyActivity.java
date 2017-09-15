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

import java.util.List;

import tech.michaelx.loadinglibrary.LoadingLayout;

/**
 * Created by michaelx on 2017/9/14.
 */

public class EmptyActivity extends AppCompatActivity implements View.OnClickListener {
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
        mRefreshLayout.setEnabled(false);
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
        mEmptyBtn.setVisibility(View.GONE);
        mCustomBtn1.setVisibility(View.GONE);
        mCustomBtn2.setVisibility(View.GONE);
    }

    private void initData() {
        // data is simulated by delaying 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // data is empty
                mLoadingLayout.showEmpty();
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, RetryActivity.class));
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerVH> {
        private List<String> mData;

        public RecyclerAdapter() {
        }

        @Override
        public RecyclerAdapter.RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerAdapter.RecyclerVH(new TextView(EmptyActivity.this));
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
