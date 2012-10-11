package com.airAd;

import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<PackageInfo> packageInfoList = null;
        try {
            packageInfoList = this.getPackageManager().getInstalledPackages(0);
        } catch (Exception e0) {
            try {
                packageInfoList = this.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
            } catch (Exception e_GET_ACTIVITIES) {
                Log.w("test", "getAppInfoList error:" + e_GET_ACTIVITIES.getMessage());
            }
        }
        if (packageInfoList != null) {
            Log.i("test", "packageSize:" + packageInfoList.size() + "");
        } else {
            Log.i("test", "packageSize:null");
        }
    }
}
