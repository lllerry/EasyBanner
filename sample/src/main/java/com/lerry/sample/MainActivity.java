package com.lerry.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lerry.library.BannerConfig;
import com.lerry.library.EasyBanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Integer[] integers = new Integer[]{R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four};
        EasyBanner easyBanner = (EasyBanner) findViewById(R.id.easy);

        easyBanner.setPages(integers, new EasyBanner.BindViewHandler() {
            @Override
            public void bind(ImageView imageView, int position) {
                imageView.setImageResource(integers[position]);
            }
        }).loop(BannerConfig.LOOP_INFINITY).autoPlay();
    }
}
