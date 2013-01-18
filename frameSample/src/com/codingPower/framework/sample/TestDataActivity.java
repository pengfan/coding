package com.codingPower.framework.sample;

import java.util.ArrayList;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.airAd.baseFrame.sample.R;
import com.codingPower.framework.sample.model.ListItemObject;

public class TestDataActivity extends BaseActivity {

    private final String TAG = "TestDataActivity";
    private ListView listView;
    private ArrayAdapter listAdapter;
    private ArrayList<ListItemObject> data = new ArrayList<ListItemObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testdata);
        listView = findView(R.id.list);
        if (savedInstanceState != null) {
            data = (ArrayList<ListItemObject>) savedInstanceState.getSerializable("dd");
        } else {
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fillData();
                }
            }, 2000);
        }
        listAdapter = new ArrayAdapter<ListItemObject>(this, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("dd", data);
    }

    private void fillData() {
        for (int i = 0; i < 10; i++) {
            data.add(new ListItemObject("item" + i));
        }
        listAdapter.notifyDataSetChanged();
    }
}
