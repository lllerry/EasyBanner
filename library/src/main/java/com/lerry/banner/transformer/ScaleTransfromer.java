package com.lerry.banner.transformer;

import android.view.View;

/**
 * Created by lerry on 15/06/2017.
 * 缩放transformer
 */

public class ScaleTransfromer implements Transformer {


    private final float SCALE_X;
    private final float SCALE_Y;

    private ScaleTransfromer(float SCALE_X, float SCALE_Y) {
        this.SCALE_X = SCALE_X;
        this.SCALE_Y = SCALE_Y;
    }

    @Override
    public void preTransform(View page, float position) {
        page.setScaleX(SCALE_X);
        page.setScaleY(SCALE_Y);
    }

    @Override
    public void onTransformingLeft(View page, float position) {
        float factorX = SCALE_X + (1 - SCALE_X) * (1 + position);
        float factorY = SCALE_Y + (1 - SCALE_Y) * (1 + position);
        page.setScaleX(factorX);
        page.setScaleY(factorY);
    }

    @Override
    public void onTransformingRight(View page, float position) {
        //0,1的时候
        float factorX = SCALE_X + (1 - SCALE_X) * (1 - position);
        float factorY = SCALE_Y + (1 - SCALE_Y) * (1 - position);
        page.setScaleX(factorX);
        page.setScaleY(factorY);
    }

    @Override
    public void nextTransform(View page, float position) {
        //如果大于1
        page.setScaleX(SCALE_X);
        page.setScaleY(SCALE_X);
    }

    public static final class Builder {
        //最小所缩放
        private static final float default_min_scale = 0.8f;
        private float SCALE_X = default_min_scale;
        private float SCALE_Y = default_min_scale;

        public Builder setScaleX(float scaleX) {
            SCALE_X = scaleX;
            return this;
        }

        public Builder setScaleY(float scaleY) {
            SCALE_Y = scaleY;
            return this;
        }

        public Builder setScale(float scale) {
            setScaleX(scale).setScaleY(scale);
            return this;
        }

        public ScaleTransfromer build() {
            return new ScaleTransfromer(SCALE_X, SCALE_Y);
        }
    }

}
