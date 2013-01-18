/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codingPower.framework.worker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.res.Resources;

import com.codingPower.framework.net.FileService;
import com.codingPower.framework.net.MySSLSocketFactory;
import com.codingPower.framework.net.RemoteService;
import com.codingPower.framework.net.Response;
import com.codingPower.framework.util.LogUtil;

/**
 * This class wraps up completing some arbitrary long running work when loading a bitmap to an
 * ImageView. It handles things like using a memory and disk cache, running the work in a background
 * thread and setting a placeholder image.
 */
public class FileNetWorker {
    private static final int TIMEOUT = 3000;

    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();
    private List<NetWorkerTask> taskList = new ArrayList<NetWorkerTask>();

    protected Resources mResources;

    public FileNetWorker(Context context) {
        mResources = context.getResources();
    }

    /**
     * 多线程处理
     * @param service
     * @param handler
     */
    public void request(final FileService service, final FileNetWorkerHandler handler) {
        if (service == null) {
            return;
        }
        NetWorkerTask netWorkerTask = new NetWorkerTask(handler);
        if (cancelPotentialWork(service, netWorkerTask)) {
            service.setTask(netWorkerTask);
            taskList.add(netWorkerTask);
            netWorkerTask.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, service);
        }
    }

    /**
     * 用于处理非多线程的情况
     * @param service
     * @param handler
     * @return
     */
    public Response directRequest(final FileService service) {
        NetWorkerTask netWorkerTask = new NetWorkerTask(null);
        return netWorkerTask.doInBackground(service);

    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    /**
     * Cancels any pending work attached to the provided ImageView.
     * @param imageView
     */
    public void cancelWork(final FileService service) {
        AsyncTask task = service.getTask();
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
            LogUtil.d(FileNetWorker.class, "cancelWork - cancelled work ");
        }
    }

    /**
     * cancel all task
     */
    public void cancelAll() {
        for (NetWorkerTask task : taskList) {
            task.cancel(true);
        }
        taskList.clear();
    }

    /**
     * 获取httpClient对象
     * @return
     */
    protected HttpClient getHttpClient() {

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }

    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, final NetWorkerTask netWorkerTask) {
        return true;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    public class NetWorkerTask extends AsyncTask<FileService, Object, Response> {

        private FileService service;
        private FileNetWorkerHandler handler;

        public NetWorkerTask(FileNetWorkerHandler handler) {
            this.handler = handler;
        }

        /**
         * Background processing.
         */
        @Override
        protected Response doInBackground(FileService... params) {

            service = params[0];
            Response response = new Response();

            String url = service.getRemoteUrl();
            if (url == null) {
                Response rsp = new Response();
                rsp.setErrorCode("-1");
                return rsp;
            }

            LogUtil.d(FileNetWorker.class, "doInBackground - starting work " + url);

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (!isCancelled() && !mExitTasksEarly) {
                int statusCode = -1;
                HttpResponse httpResponse = null;
                try {
                    HttpClient httpClient = getHttpClient();
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
                    HttpRequestBase httpRequest = null;
                    switch (service.getType()) {
                        case RemoteService.TYPE_POST : {
                            HttpPost post = new HttpPost(service.getRemoteUrl());
                            post.setEntity(service.getRequestEntity());
                            httpRequest = post;
                            break;
                        }
                        case RemoteService.TYPE_GET : {
                            httpRequest = new HttpGet(service.getRemoteUrl());
                            break;
                        }
                        case RemoteService.TYPE_DELETE : {
                            httpRequest = new HttpDelete(service.getRemoteUrl());
                            break;
                        }
                    }
                    for (Header header : service.getHeaderList()) {
                        httpRequest.addHeader(header);
                    }
                    httpResponse = httpClient.execute(httpRequest);
                    statusCode = httpResponse.getStatusLine().getStatusCode();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.setStatus(statusCode);
                if ((statusCode == 200 || statusCode == 206) && !isCancelled() && !mExitTasksEarly) {
                    synchronized (service) {
                        long size =
                                downloadFile(httpResponse.getEntity(), service.getDownloadFile(), service.getRange(), service.getSize());
                        service.completeDownload(size, response);
                    }
                } else {
                    LogUtil.w(FileNetWorker.class, "statusCode:" + statusCode);
                }
            }
            LogUtil.d(FileNetWorker.class, "doInBackground - finished work " + url);
            return response;
        }

        private long downloadFile(HttpEntity res, File downloadFile, Long range, int size) {
            RandomAccessFile output = null;
            InputStream input = null;
            long total = 0;
            try {
                if (res.isStreaming() && downloadFile != null) {
                    long allLength = res.getContentLength();
                    output = new RandomAccessFile(downloadFile, "rwd");
                    if (range != null) {
                        output.seek(range);
                    } else {
                        //如果range为0，则清空原有文件
                        range = 0L;
                        output.setLength(0);
                    }
                    allLength += range;
                    input = new BufferedInputStream(res.getContent(), size * 2);
                    byte data[] = new byte[size];
                    int count = 0;
                    while ((count = input.read(data)) != -1) {
                        if (isCancelled()) {
                            break;
                        }
                        total += count;
                        output.write(data, 0, count);
                        publishProgress(range + total, allLength);
                        LogUtil.i(FileService.class, "out : " + (range + total) + "," + allLength);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return total;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if (handler != null) {
                handler.progressUpdate(values);
            }
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Response data) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            LogUtil.i(FileNetWorker.class, "remove");
            taskList.remove(this);
            if (mExitTasksEarly) {
                return;
            }
            if (handler != null) {
                handler.handleData(data);
            }
        }

        @Override
        protected void onCancelled(Response rsp) {
            super.onCancelled(rsp);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

    }

    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

}
