package com.codingPower.framework.sample;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HomeActivity extends ListActivity {

    private static Item[] objs = new Item[] { new Item(NetWorkerTestActivity.class, "netWorker"),
            new Item(TestDataActivity.class, "testData") };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setListAdapter(new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, objs));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        startActivity(new Intent(this, objs[position].clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class Item {
        public Class<? extends Activity> clazz;
        public String name;

        public Item(Class<? extends Activity> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

}
