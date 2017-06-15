package com.lerry.banner.util;


import android.content.Context;

/**
 * Created by lerry on 12/06/2017.
 */

public class ConvertUtils {
    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
