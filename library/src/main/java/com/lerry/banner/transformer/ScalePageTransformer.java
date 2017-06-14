package com.lerry.banner.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by lerry on 12/06/2017.
 * 放大的viewpager动画
 */

public class ScalePageTransformer implements ViewPager.PageTransformer {
    //最小所缩放
    private static final float default_min_scale = 0.8f;

    /**
     * position的作用
     * [-Infinity,-1) 为左边区域
     * (1,+Infinity] 为右边区域
     * [-1,1]中间区域
     */
    @Override
    public void transformPage(View page, float position) {
        if (position < -1) {
            //为左边区域
//            page.setScaleX(default_min_scale);
            page.setScaleY(default_min_scale);
        } else if (position <= 1) {
            //如果为-1,1 根据position变换 0的时候为1
            if (position < 0) {
                //-1,0之间缩小
                float factor = default_min_scale + (1 - default_min_scale) * (1 + position);
//                page.setScaleX(factor);
                page.setScaleY(factor);
            } else {
                //0,1的时候
                float factor = default_min_scale + (1 - default_min_scale) * (1 - position);
//                page.setScaleX(factor);
                page.setScaleY(factor);
            }
        } else {
            //如果大于1
//            page.setScaleX(default_min_scale);
            page.setScaleY(default_min_scale);
        }

    }
}
