package com.codingPower.framework.sample;

import android.app.Activity;
import android.view.View;

public class BaseActivity extends Activity {

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }
}
