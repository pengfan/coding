package com.codingPower.framework.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;

/**
 * 动画改变类
 * @author pengfan
 *
 */
public class AnimationUtil {
    private static AnimationSet showAction;
    private static AnimationSet hideAction;

    private static void initAnimation() {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        showAction = new AnimationSet(false);
        showAction.addAnimation(animation);

        animation = new AlphaAnimation(1.0f, 0.2f);
        animation.setDuration(2000);
        hideAction = new AnimationSet(false);
        hideAction.addAnimation(animation);
    }

    public static void showImage(View View, AnimationListener listener) {
        if (showAction == null) {
            initAnimation();
        }
        if (View.getVisibility() != View.VISIBLE) {
            View.setVisibility(View.VISIBLE);
            showAction.setAnimationListener(listener);
            View.startAnimation(showAction);
        }
    }

    public static void switchView(View view1, View view2, AnimationListener listener) {
        if (hideAction == null) {
            initAnimation();
        }
        if (view2.getVisibility() != View.VISIBLE) {
            view2.setVisibility(View.VISIBLE);
            view1.startAnimation(hideAction);
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setStartOffset(1200);
            animation.setDuration(800);
            animation.setAnimationListener(listener);
            view2.startAnimation(animation);
        }

    }
}
