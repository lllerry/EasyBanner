package com.lerry.banner.transformer;

import android.view.View;

/**
 * Created by lerry on 15/06/2017.
 */

public class ScaleTransfromer implements Transformer {
    //最小所缩放
    private static final float default_min_scale = 0.8f;

    @Override
    public void preTransform(View page, float position) {
//            page.setScaleX(default_min_scale);
        page.setScaleY(default_min_scale);
    }

    @Override
    public void onTransformingLeft(View page, float position) {
        float factor = default_min_scale + (1 - default_min_scale) * (1 + position);
//                page.setScaleX(factor);
        page.setScaleY(factor);
    }

    @Override
    public void onTransformingRight(View page, float position) {
        //0,1的时候
        float factor = default_min_scale + (1 - default_min_scale) * (1 - position);
//                page.setScaleX(factor);
        page.setScaleY(factor);
    }

    @Override
    public void nextTransform(View page, float position) {
        //如果大于1
//            page.setScaleX(default_min_scale);
        page.setScaleY(default_min_scale);
    }
}
