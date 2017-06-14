package com.lerry.library;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lerry on 14/06/2017.
 * 轮播图
 */

public class EasyBanner extends FrameLayout {

    private Context mContext;
    private ViewPager mViewpager;
    private ViewPagerAdapter mAdapter;

    //默认循环方式是
    private static int LOOP_TYPE = BannerConfig.LOOP_DEFAULT;

    public EasyBanner(@NonNull Context context) {
        this(context, null);
    }

    public EasyBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public EasyBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.banner_layout, this, true);
        mAdapter = ViewPagerAdapter.create();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mViewpager.setOffscreenPageLimit(3);
    }


    //设置数据
    public <T> EasyBanner setPages(T[] pages, BindViewHandler bindViewHandler) {
        setPages(Arrays.asList(pages), bindViewHandler);
        return this;
    }

    //设置数据
    public <T> EasyBanner setPages(List<T> datas, BindViewHandler bindViewHandler) {
        mAdapter.setData(datas, bindViewHandler);
        mViewpager.setAdapter(mAdapter);
        return this;
    }

    //设置当前页数
    public EasyBanner firstShowMidItem() {
        mViewpager.setCurrentItem(mAdapter.getCount() / 2);
        return this;
    }

    //设置循环
    public EasyBanner loop(int type) {
        LOOP_TYPE = type;
        mAdapter.notifyDataSetChanged();
        return this;
    }

    //设置自动轮播
    public EasyBanner autoPlay() {
        if (LOOP_TYPE != BannerConfig.LOOP_INFINITY) {
            throw new IllegalStateException("only loop_infinity can start autoplay");
        }
        firstShowMidItem();
        //开启轮播
        startAutoPlay();
        return this;
    }

    private void startAutoPlay() {
        mhandler.post(mRunnable);
    }

    private static Handler mhandler = new Handler();

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = mViewpager.getCurrentItem();
            currentItem++;
            if (currentItem == mAdapter.getCount() - 1) {
                currentItem = 0;
                mViewpager.setCurrentItem(currentItem, false);
                mhandler.postDelayed(this, BannerConfig.DEFALUT_LOOP_TIME);
            } else {
                mViewpager.setCurrentItem(currentItem);
                mhandler.postDelayed(this, BannerConfig.DEFALUT_LOOP_TIME);
            }
        }
    };

    static class ViewPagerAdapter<T> extends PagerAdapter {
        private List<T> mDatas;
        private BindViewHandler mBindViewHandler;
        private final int MAX_VALUE = 1000;

        private void setData(List<T> datas, BindViewHandler bindViewHandler) {
            mDatas = datas;
            mBindViewHandler = bindViewHandler;
        }


        @NonNull
        private static ViewPagerAdapter create() {
            return new ViewPagerAdapter();
        }

        @Override
        public int getCount() {
            return LOOP_TYPE == 1 ? mDatas.size() : MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getView(container, position);
            container.addView(view);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public int getRealCount() {
            return mDatas.size();
        }

        private View getView(ViewGroup container, int position) {
            int realPosition = LOOP_TYPE == 1 ? position : position % getRealCount();
            //设置数据
            ImageView imageView = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.viewpager_item, container, false);
            //回调设置数据
            mBindViewHandler.bind(imageView, realPosition);
            return imageView;
        }

    }

    public interface BindViewHandler {
        void bind(ImageView imageView, int position);
    }
}