package com.lerry.banner;

import android.content.Context;
import android.content.res.TypedArray;
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

import com.lerry.banner.transformer.Transformer;
import com.lerry.banner.util.ConvertUtils;

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
    private boolean isAutoPlay = true; //是否轮播

    //默认循环方式是
    private static int LOOP_TYPE = BannerConfig.LOOP_DEFAULT;

    private long mDelayTime = BannerConfig.DEFALUT_LOOP_TIME;
    //4个边距
    private int mLeft_space;
    private int mRight_space;
    //循环还是普通轮播图
    private int mMode;
    //页面间距
    private int mPage_margin;


    public EasyBanner(@NonNull Context context) {
        this(context, null);
    }

    public EasyBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public EasyBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyBanner, defStyleAttr, -1);
        mLeft_space = (int) typedArray.getDimension(R.styleable.EasyBanner_left_space, 0);
        mRight_space = (int) typedArray.getDimension(R.styleable.EasyBanner_right_space, 0);
//        mMode = typedArray.getInt(R.styleable.EasyBanner_mode, 0);
        mPage_margin = (int) typedArray.getDimension(R.styleable.EasyBanner_page_margin, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.banner_layout, this, true);
        mAdapter = ViewPagerAdapter.create();
    }

    //解决wrap_content无效问题
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //根据状态,切换是否自动轮播
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    //被拖拽的时候停止自动轮播
                    isAutoPlay = false;
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    //空闲的时候开启自动轮播
                    isAutoPlay = true;
                    break;
            }
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) mViewpager.getLayoutParams();
        layoutParams.setMargins(mLeft_space, 0, mRight_space, 0);
        mViewpager.requestLayout();
        mViewpager.setOffscreenPageLimit(3);
        mViewpager.setPageMargin(ConvertUtils.dp2px(mContext, mPage_margin));
        mViewpager.addOnPageChangeListener(mOnPageChangeListener);
        initViewPagerScroll();

//        if (mMode == 1) {
//            //轮训
//            loop(BannerConfig.LOOP_INFINITY).autoPlay().setDelayTime(mDelayTime);
//        }
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

    //设置数据
    public <T> EasyBanner setPages(T[] pages, BindViewHandler bindViewHandler) {
        return setPages(Arrays.asList(pages), bindViewHandler);
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


    public EasyBanner setAnimation(Transformer transformer) {

        mViewpager.setPageTransformer(false,
                new PageTransformer.Builder().transfromer(transformer).build());

        return this;
    }

    private void startAutoPlay() {
        mhandler.post(mRunnable);
    }

    private static Handler mhandler = new Handler();


    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
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
            } else {
                //发送消息，不轮播
                mhandler.postDelayed(this, mDelayTime);
            }

        }
    };

    //设置轮播间隔事件
    public EasyBanner setDelayTime(long delayTime) {
        mDelayTime = delayTime < 1500 ? 1500 : delayTime;
        return this;
    }

    //设置page margin
    public EasyBanner setPageMargin(int marginPixels) {
        mViewpager.setPageMargin(marginPixels);
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

        //真实的图片的数量
        int getRealCount() {
            return mDatas.size();
        }

        private View getView(ViewGroup container, int position) {
            final int realPosition = LOOP_TYPE == 1 ? position : position % getRealCount();
            //
            //设置数据
            ImageView imageView = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.viewpager_item, container, false);

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBindViewHandler != null) {
                        mBindViewHandler.onClick(v, realPosition);
                    }
                }
            });
            //回调设置数据
            if (mBindViewHandler != null) {
                mBindViewHandler.bind(imageView, realPosition);
            }
            return imageView;
        }
    }

    //设置viewpager滚动的动画时间
    private static class ViewPagerScroller extends Scroller {
        //默认duration
        private int mDuration = 1000;

        ViewPagerScroller(Context context) {
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

        void onClick(View view, int position);
    }
}
