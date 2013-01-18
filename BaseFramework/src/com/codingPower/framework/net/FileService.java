package com.codingPower.framework.net;

import java.io.File;

import org.apache.http.HttpResponse;

import com.codingPower.framework.worker.AsyncTask;

public abstract class FileService extends BasicService {
    private File downloadFile;
    private int size = 1024;
    private AsyncTask task;
    private Long range;

    public FileService(int type) {
        super(type);
    }

    public Long getRange() {
        return range;
    }

    public void setRange(Long range) {
        this.range = range;
        addHeader("RANGE", "bytes=" + range + "-");
    }

    public void setTask(AsyncTask task) {
        this.task = task;
    }

    public AsyncTask getTask() {
        return task;
    }

    public File getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public abstract String getRemoteUrl();

    public abstract void completeDownload(long size, Response rsp);

    @Override
    public void handleResponse(HttpResponse httpRsp, Response rsp) {

    }

}
