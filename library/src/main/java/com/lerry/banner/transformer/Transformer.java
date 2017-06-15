package com.lerry.banner.transformer;

import android.view.View;

/**
 * Created by lerry on 15/06/2017.
 */

public interface Transformer {
    /**
     * [-Infinity,-1) 为左边区域
     */
    void preTransform(View page, float position);

    /**
     * [-1,0)中间区域
     */
    void onTransformingLeft(View page, float position);

    /**
     * [0,1]中间区域
     */
    void onTransformingRight(View page, float position);

    /**
     * (1,+Infinity] 为右边区域
     */
    void nextTransform(View page, float position);
}
