
package com.codingPower.ui;

import android.app.Activity;
import android.os.Bundle;

import com.codingPower.R;
import com.codingPower.ui.view.RotatView;

public class RoateActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roate_main);
        RotatView rotatView=(RotatView)findViewById(R.id.myRotatView);
        rotatView.setRotatDrawableResource(R.drawable.cycle);
    }

}
