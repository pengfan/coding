package com.codingPower.framework.net;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;

/**
 * 所有数据接口层的父类
 * @author pengfan
 *
 */
public abstract class RemoteService {

    /** 
     * 字符集
     */
    public static final String CHARSET = "UTF-8";

    public static final int RES_OK = 200;
    public static final int RES_FAIL = -1;

    public static final int TYPE_POST = 0;
    public static final int TYPE_GET = 1;
    public static final int TYPE_DELETE = 2;

    private int type = TYPE_POST;

    private List<BasicHeader> headerList = new ArrayList<BasicHeader>();

    public RemoteService() {
    }

    public RemoteService(int type) {
        this.type = type;
    }

    /**
     * 获取远程url
     * @return
     */
    public abstract String getRemoteUrl();

    /**
     * 获取httpEntity
     * @return
     */
    public abstract HttpEntity getRequestEntity();

    /**
     * 将字符串参数置入
     * @param name
     * @param value
     */
    public abstract void putString(String name, Object value);

    /**
     * 获取数据
     * @param name
     * @return
     */
    public abstract String getString(String name);

    /**
     * 处理响应数据
     * @param res
     * @return
     */
    public abstract void handleResponse(HttpResponse httpRsp, Response rsp);

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 增加headerList
     * @return
     */
    public List<BasicHeader> getHeaderList() {
        return headerList;
    }

    public void addHeader(String name, String value) {
        headerList.add(new BasicHeader(name, value));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RemoteService) {
            if (getRemoteUrl().equals(((RemoteService) o).getRemoteUrl())) {
                return true;
            }
        }
        return false;
    }

}
