package com.codingPower.framework.sample;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airAd.baseFrame.sample.R;
import com.codingPower.framework.net.BasicService;
import com.codingPower.framework.net.Response;
import com.codingPower.framework.worker.FileNetWorker;
import com.codingPower.framework.worker.FileNetWorkerHandler;

public class NetWorkerTestActivity extends BaseActivity {
    /** Called when the activity is first created. */
    private final String TAG = "NetWorkerTest";
    private TextView textView;
    private FileNetWorker netWorker;
    private int count = 0;
    private ProgressBar progressBar;
    public static final String parentFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/0ticket/";
    private static File file = new File(parentFolderPath, "1.swf");
    private List<MyFileService> serviceList = new ArrayList<MyFileService>();
    private MyFileService service = null;
    private Long last;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.networker);
        textView = findView(R.id.test_show);
        progressBar = findView(R.id.test_progressbar);
        netWorker = new FileNetWorker(this);
    }

    public void start(View view) {
        /*TestRemoteService service = new TestRemoteService();
        service.setName("btn" + count++);
        netWorker.request(service, new NetWorkerHandler<Response, Long>() {

            @Override
            public void handleData(Response rsp) {
                String t = (String) rsp.getData();
                textView.append("\n" + t);
                Log.i(TAG, "output to " + t);
            }

            @Override
            public void progressUpdate(Long value1, Long value2) {

            }
        });*/
        service = new MyFileService();
        service.setDownLoadURL("http://img.airad.com/opai/v/1.swf");
        service.setDownloadFile(file);
        if (last != null) {
            service.setRange(last);
        } else {
            progressBar.setProgress(0);
            file.delete();
        }
        //final int index = serviceList.indexOf(service);

        /*        progressBar.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        netWorker.cancelWork(serviceList.get(index));
                    }
                });*/
        netWorker.request(service, new FileNetWorkerHandler() {

            @Override
            public void handleData(Response rsp) {
                Long l = (Long) rsp.getData();
                textView.append("\n" + String.valueOf(l));
                Log.i(TAG, "output to " + String.valueOf(l));
            }

            @Override
            public void progressUpdate(Object[] vals) {
                long current = (Long) vals[0];
                long allLength = (Long) vals[1];
                progressBar.setProgress((int) (current * 100 / allLength));
                textView.setText(current + "/" + allLength);
                last = current;
            }
        });
    }

    public void stop(View view) {
        if (service != null) {
            netWorker.cancelWork(service);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://emms.airad.com/airPass.apk"));
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (MyFileService service : serviceList) {
            netWorker.cancelWork(service);
        }
    }

    private class TestRemoteService extends BasicService {
        private String name;

        @Override
        public String getRemoteUrl() {
            return "http://stackoverflow.com/";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void handleResponse(HttpResponse httpRsp, Response rsp) {
            rsp.setData(name);

        }

    }
}
