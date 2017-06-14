package com.lerry.banner;

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
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;

import com.lerry.banner.transformer.ScalePageTransformer;

import java.lang.reflect.Field;
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

    private long mDelayTime = BannerConfig.DEFALUT_LOOP_TIME;

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


    private void initViewPagerScroll() {
        Field mScroller;
        try {
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller viewPagerScroller = new ViewPagerScroller(mContext);
            mScroller.set(mViewpager, viewPagerScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mViewpager.setOffscreenPageLimit(3);
        initViewPagerScroll();
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

    public EasyBanner setAnimation(TransformerMode transformerMode) {
        switch (transformerMode) {
            case Scale:
                mViewpager.setPageTransformer(false, new ScalePageTransformer());
            case Alpha:
                break;
            default:
                break;
        }
        return this;
    }

    public enum TransformerMode {

        Scale, Alpha

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
                mhandler.postDelayed(this, mDelayTime);
            } else {
                mViewpager.setCurrentItem(currentItem);
                mhandler.postDelayed(this, mDelayTime);
            }
        }
    };

    //设置轮播间隔事件
    public EasyBanner setDelayTime(long delayTime) {
        mDelayTime = delayTime < 1500 ? 1500 : delayTime;
        return this;
    }

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


    //设置滚动的事件
    static class ViewPagerScroller extends Scroller {
        //默认duration
        private int mDuration = 1000;

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration > 1500 ? 1500 : mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration > 1500 ? 1500 : mDuration);
        }
    }

    public interface BindViewHandler {
        void bind(ImageView imageView, int position);
    }
}
