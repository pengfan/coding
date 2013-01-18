package com.codingPower.framework.sample;

import com.codingPower.framework.net.FileService;
import com.codingPower.framework.net.Response;

public class MyFileService extends FileService {

    private String url;

    public MyFileService() {
        super(TYPE_GET);
        setSize(1024);
    }

    public void setDownLoadURL(String url) {
        this.url = url;
    }

    @Override
    public String getRemoteUrl() {
        return url;
    }

    @Override
    public void completeDownload(long size, Response rsp) {

    }

}
