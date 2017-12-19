package tech.michaelx.loadinglibrary;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnimatorRes;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Author: michaelx
 * Create: 2017/9/7.
 * <p>
 * Description:多状态加载布局
 * <p>
 * 1. 加载中
 * 2. 加载完成
 * 3. 加载失败
 * 4. 数据为空
 * <p>
 * Blog:http://blog.csdn.net/xiong_it | https://xiong-it.github.io
 * github:https://github.com/xiong-it
 * <p>
 */


public class LoadingLayout extends FrameLayout implements View.OnClickListener {
    // 默认的加载进度圈
    private ProgressBar mLoadingBar;
    private Drawable mProgressDrawable;
    // 加载过程中显示的控件
    private View mLoadingView;
    // 加载失败显示的控件
    private View mErrorView;
    // 没有数据显示的控件
    private View mEmptyView;
    // 数据为空时图片
    private Drawable mEmptyDrawable;
    // 数据为空时提示语
    private String mEmptyText;
    // 加载失败时的图片
    private Drawable mErrorDrawable;
    // 加载失败时的提示语
    private String mErrorText;
    // 是否打开自动加载调试
    private boolean mAutoLoadingDebug = false;

    // 是否总是点击重试，无论数据为空或者失败，默认只有在加载失败时需要点击重试
    private boolean mRetryLoadAlways;

    // mLoadingView加载使用的动画
    private Animator mLoadingAnim;
    // 原本背景
    private Drawable mBackground;
    // 加载过程中显示的背景
    private Drawable mLoadingBackground;

    // 用以存储控件显示状态
    private ArrayMap<View, Integer> mVisibilityMap = new ArrayMap<>();

    // 重试监听
    private OnRetryLoadListener mReLoadListener;

    public LoadingLayout(@NonNull Context context) {
        this(context, null);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 事先保存子控件显示状态，并隐藏所有子控件
        for (int i = 0; i < getChildCount(); i++) {
            mVisibilityMap.put(getChildAt(i), getChildAt(i).getVisibility());
            if (!mAutoLoadingDebug) {
                getChildAt(i).setVisibility(GONE);
            }
        }

        mLoadingBar = new ProgressBar(getContext());
        mLoadingBar.setIndeterminate(true);
        if (mProgressDrawable != null) {
            mLoadingBar.setProgressDrawable(mProgressDrawable);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        addView(mLoadingBar, params);

        if (!mAutoLoadingDebug) {
            showLoading();
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mBackground = getBackground();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, defStyleAttr, 0);

        int loadingViewLayoutId = ta.getResourceId(R.styleable.LoadingLayout_loadingView, 0);
        int emptyViewLayoutId = ta.getResourceId(R.styleable.LoadingLayout_emptyView, 0);
        int errorViewLayoutId = ta.getResourceId(R.styleable.LoadingLayout_errorView, 0);
        int loadingAnimId = ta.getResourceId(R.styleable.LoadingLayout_loadingAnimator, 0);
        mLoadingBackground = ta.getDrawable(R.styleable.LoadingLayout_loadingBackground);
        mProgressDrawable = ta.getDrawable(R.styleable.LoadingLayout_loadingProgressDrawable);
        mRetryLoadAlways = ta.getBoolean(R.styleable.LoadingLayout_retryLoadAlways, false);
        mAutoLoadingDebug = ta.getBoolean(R.styleable.LoadingLayout_showLoadingDebug, false);

        try {
            mLoadingView = LayoutInflater.from(context).inflate(loadingViewLayoutId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mEmptyView = LayoutInflater.from(context).inflate(emptyViewLayoutId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mErrorView = LayoutInflater.from(context).inflate(errorViewLayoutId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mLoadingAnim = AnimatorInflater.loadAnimator(context, loadingAnimId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
            if (mEmptyView != null) {
                TextView emptyTv = (TextView) mEmptyView.findViewById(R.id.empty_text);
                mEmptyText = emptyTv.getText().toString().trim();
                ImageView emptyImg = (ImageView) mEmptyView.findViewById(R.id.empty_img);
                mEmptyDrawable = emptyImg.getBackground();
            }

            Drawable emptyDrawable = ta.getDrawable(R.styleable.LoadingLayout_emptyDrawable);
            if (emptyDrawable != null) {
                setEmptyDrawable(emptyDrawable);
            }
            String emptyText = ta.getString(R.styleable.LoadingLayout_emptyText);
            if (emptyText != null) {
                setEmptyText(emptyText);
            }
        }

        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(context).inflate(R.layout.failure_view, null);
            if (mErrorView != null) {
                TextView errorTv = (TextView) mErrorView.findViewById(R.id.failure_text);
                mErrorText = errorTv.getText().toString().trim();
                ImageView errorImg = (ImageView) mErrorView.findViewById(R.id.failure_img);
                mErrorDrawable = errorImg.getBackground();
            }

            Drawable errorDrawable = ta.getDrawable(R.styleable.LoadingLayout_errorDrawable);
            if (errorDrawable != null) {
                setErrorDrawable(errorDrawable);
            }
            String errorText = ta.getString(R.styleable.LoadingLayout_errorText);
            if (errorText != null) {
                setErrorText(errorText);
            }
        }

        ta.recycle();

        if (mLoadingView != null) {
            setLoadingView(mLoadingView);
        }

        if (mEmptyView != null) {
            setEmptyView(mEmptyView);
        }

        if (mErrorView != null) {
            setErrorView(mErrorView);
        }

        if (mLoadingBackground == null) {
            mLoadingBackground = new ColorDrawable(ResourcesCompat.getColor(getResources(),
                    android.R.color.white, context.getTheme()));
        }
    }

    public void setLoadingView(@LayoutRes int layoutId) {
        mLoadingView = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setLoadingView(mLoadingView);
    }

    public void setLoadingView(@NonNull View loadingView) {
        mLoadingView = loadingView;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(mLoadingView, params);
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public void setLoadingAnim(@AnimatorRes int animId) {
        mLoadingAnim = AnimatorInflater.loadAnimator(getContext(), animId);
    }

    public void setLoadingAnim(Animator anim) {
        mLoadingAnim = anim;
    }

    public void setEmptyView(@LayoutRes int layoutId) {
        mEmptyView = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setEmptyView(mEmptyView);
    }

    public void setEmptyView(@NonNull View emptyView) {
        mEmptyView = emptyView;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(mEmptyView, params);
        mEmptyView.setOnClickListener(this);
        mEmptyView.setVisibility(GONE);
    }

    public void setErrorView(@LayoutRes int layoutId) {
        mErrorView = LayoutInflater.from(getContext()).inflate(layoutId, null);
        setErrorView(mErrorView);
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setErrorView(@NonNull View errorView) {
        mErrorView = errorView;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(mErrorView, params);
        mErrorView.setOnClickListener(this);
        mErrorView.setVisibility(GONE);
    }

    public View getErrorView() {
        return mErrorView;
    }

    public Drawable getEmptyDrawable() {
        return mEmptyDrawable;
    }

    public void setEmptyDrawable(Drawable emptyDrawable) {
        this.mEmptyDrawable = emptyDrawable;
        if (mEmptyView != null && mEmptyDrawable != null) {
            ImageView empty = (ImageView) mEmptyView.findViewById(R.id.empty_img);
            if (empty != null) {
                ResCompat.setBackground(empty, mEmptyDrawable);
            }
        }
    }

    public void setEmptyDrawable(@DrawableRes int drawableId) {
        setEmptyDrawable(ResCompat.getDrawable(this, drawableId));
    }

    public String getEmptyText() {
        return mEmptyText;
    }

    public void setEmptyText(String emptyText) {
        this.mEmptyText = emptyText;
        if (mEmptyView != null && emptyText != null) {
            TextView empty = (TextView) mEmptyView.findViewById(R.id.empty_text);
            if (empty != null) {
                empty.setText(mEmptyText);
            }
        }
    }

    public void setEmptyText(@StringRes int strId) {
        setEmptyText(getResources().getText(strId).toString());
    }

    public Drawable getErrorDrawable() {
        return mErrorDrawable;
    }

    public void setErrorDrawable(Drawable errorDrawable) {
        this.mErrorDrawable = errorDrawable;
        if (mErrorView != null && mErrorDrawable != null) {
            ImageView error = (ImageView) mErrorView.findViewById(R.id.failure_img);
            if (error != null) {
                ResCompat.setBackground(error, mErrorDrawable);
            }
        }
    }

    public void setErrorDrawable(@DrawableRes int drawableId) {
        setErrorDrawable(ResCompat.getDrawable(this, drawableId));
    }

    public String getErrorText() {
        return mErrorText;
    }

    public void setErrorText(String errorText) {
        this.mErrorText = errorText;
        if (mErrorView != null && mErrorText != null) {
            TextView error = (TextView) mErrorView.findViewById(R.id.failure_text);
            if (error != null) {
                error.setText(mErrorText);
            }
        }
    }

    public void setErrorText(@StringRes int strId) {
        setErrorText(getResources().getText(strId).toString());
    }

    public boolean isRetryLoadAlways() {
        return mRetryLoadAlways;
    }

    public void setRetryLoadAlways(boolean retryLoadAlways) {
        this.mRetryLoadAlways = retryLoadAlways;
        if (mRetryLoadAlways && mEmptyView != null) {
            mEmptyView.setOnClickListener(this);
        }
    }

    /**
     * 加载中
     */
    public void showLoading() {
        if (mLoadingBackground != null) {
            ResCompat.setBackground(this, mLoadingBackground);
        }

        if (mLoadingView != null) {
            mLoadingView.setVisibility(VISIBLE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(GONE);
        }

        if (mLoadingView != null && mLoadingAnim != null) {
            mLoadingBar.setVisibility(GONE);
            mLoadingAnim.setTarget(mLoadingView);
            mLoadingAnim.start();
            return;
        }

        if (mLoadingBar != null) {
            mLoadingBar.setVisibility(VISIBLE);
        }
    }

    /**
     * 加载完成
     */
    public void loadComplete() {
        if (mBackground != null) {
            ResCompat.setBackground(this, mBackground);
        }

        for (View view : mVisibilityMap.keySet()) {
            int visibility = (mVisibilityMap.get(view) == VISIBLE ? VISIBLE :
                    (mVisibilityMap.get(view) == GONE ? GONE : INVISIBLE));
            view.setVisibility(visibility);
        }

        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(GONE);
        }
        if (mLoadingBar != null) {
            mLoadingBar.setVisibility(GONE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(GONE);
        }
    }

    /**
     * 加载失败
     */
    public void loadFailure() {
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(GONE);
        }
        if (mLoadingBar != null) {
            mLoadingBar.setVisibility(GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(GONE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(VISIBLE);
        }
    }

    /**
     * 数据为空
     */
    public void showEmpty() {
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mLoadingView != null) {
            mLoadingView.setVisibility(GONE);
        }
        if (mLoadingBar != null) {
            mLoadingBar.setVisibility(GONE);
        }
        if (mErrorView != null) {
            mErrorView.setVisibility(GONE);
        }
        if (mEmptyView != null) {
            mEmptyView.setVisibility(VISIBLE);
        }
    }

    public void setOnRetryLoadListener(@NonNull OnRetryLoadListener retryLoadListener) {
        mReLoadListener = retryLoadListener;
    }

    @Override
    public void onClick(View v) {
        if (v == mErrorView) {
            reload();
        }
        if (v == mEmptyView && mRetryLoadAlways) {
            reload();
        }
    }

    private void reload() {
        if (mReLoadListener != null) {
            showLoading();
            mReLoadListener.onReLoad();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mReLoadListener != null) {
            mReLoadListener = null;
        }
        if (mLoadingAnim != null) {
            mLoadingAnim.cancel();
        }
        if (mVisibilityMap != null) {
            mVisibilityMap.clear();
        }
    }

    public interface OnRetryLoadListener {
        void onReLoad();
    }

}
