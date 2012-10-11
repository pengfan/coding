package com.codingPower.ui;

import android.app.Activity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.scoller);
        bannerScrollView = (ScrollView) findViewById(R.id.sceneScroll);
        bannerLayout = (LinearLayout) findViewById(R.id.sceneLayout);
        flinger = new Flinger();
        super.onCreate(savedInstanceState);
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
