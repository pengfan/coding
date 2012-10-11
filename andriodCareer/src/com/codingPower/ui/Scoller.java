package com.codingPower.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.codingPower.R;

/**
 * 利用系统api中计算距离的方法，实现scoller的平滑滚动
 * @author pengfan
 *
 */
public class Scoller extends Activity {

    private ScrollView bannerScrollView; //图片滚动层
    private LinearLayout bannerLayout;//图片容器
    private Flinger flinger;
    private int flingHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.scoller);
        flingHeight = getResources().getDrawable(R.drawable.photo_02).getIntrinsicHeight();
        bannerScrollView = (ScrollView) findViewById(R.id.sceneScroll);
        LayoutParams params = bannerScrollView.getLayoutParams();
        params.height = flingHeight;
        bannerScrollView.setLayoutParams(params);
        bannerLayout = (LinearLayout) findViewById(R.id.sceneLayout);
        flinger = new Flinger();

        super.onCreate(savedInstanceState);
        initBanner();
        bannerLayout.setClickable(true);
        bannerLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                flinger.start(flingHeight);
            }
        });
    }

    private void initBanner() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.photo_02);
        bannerLayout.addView(imageView);
        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.photo_03);
        bannerLayout.addView(imageView);

    }
    /**
     * 自定义class用于平滑滚动，
     * @author pengfan
     *
     */
    private class Flinger implements Runnable {
        //该类的作用仅仅是计算移动距离的，移动还需要使用线程在Runnable的run方法中完成
        private final Scroller scroller;

        Flinger() {
            scroller = new Scroller(Scoller.this);
        }

        void start(int distance) {
            int initialy = bannerScrollView.getScrollY();
            scroller.startScroll(0, initialy, 0, distance - initialy, 400);
            //TODO:了解view的post方法意义。
            bannerScrollView.post(this);
        }

        public void run() {
            if (scroller.isFinished()) {
                bannerLayout.removeViewAt(0);
                return;
            }

            boolean more = scroller.computeScrollOffset();
            int y = scroller.getCurrY();
            bannerScrollView.scrollTo(0, y);
            if (more) {
                bannerScrollView.post(this);
            }
        }

        boolean isFlinging() {
            return !scroller.isFinished();
        }

        void forceFinished() {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
        }
    }
}
