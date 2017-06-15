package com.lerry.banner;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.lerry.banner.transformer.DefaultTransformer;
import com.lerry.banner.transformer.Transformer;

/**
 * Created by lerry on 12/06/2017.
 */

public final class PageTransformer implements ViewPager.PageTransformer {

    private final Transformer mTransformer;

    private PageTransformer(Transformer transformer) {
        mTransformer = transformer;
    }

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
            mTransformer.preTransform(page, position);
        } else if (position <= 1) {
            //如果为-1,1 根据position变换 0的时候为1
            if (position < 0) {
                //-1,0之间缩小
                mTransformer.onTransformingLeft(page, position);
            } else {
                mTransformer.onTransformingRight(page, position);
            }
        } else {
            mTransformer.nextTransform(page, position);
        }
    }


    public static final class Builder {
        private Transformer mTransformer;

        public Builder transfromer(Transformer transformer) {
            if (transformer == null) {
                throw new IllegalArgumentException("Illegal transformer: " + transformer);
            }
            this.mTransformer = transformer;
            return this;
        }

        public PageTransformer build() {
            if (mTransformer == null) {
                mTransformer = new DefaultTransformer();
            }
            return new PageTransformer(mTransformer);
        }

    }
}
