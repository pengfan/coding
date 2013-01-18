package com.codingPower.framework.net;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * 普通参数的service父类
 * @author pengfan
 *
 */
public abstract class BasicService extends RemoteService {

    private List<BasicNameValuePair> list;

    public BasicService() {
        super();
        list = new ArrayList<BasicNameValuePair>();
    }

    public BasicService(int type) {
        super(type);
        list = new ArrayList<BasicNameValuePair>();
    }

    @Override
    public void putString(String name, Object value) {
        list.add(new BasicNameValuePair(name, String.valueOf(value)));
    }

    @Override
    public String getString(String name) {
        for (BasicNameValuePair pair : list) {
            if (pair.getName().equals(name)) {
                return pair.getValue();
            }
        }
        return null;
    }

    @Override
    public HttpEntity getRequestEntity() {
        try {
            return new UrlEncodedFormEntity(list, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用于get请求将参数拼成字符串
     */
    public String getStrParams() {
        return "?" + URLEncodedUtils.format(list, CHARSET);
    }

}
