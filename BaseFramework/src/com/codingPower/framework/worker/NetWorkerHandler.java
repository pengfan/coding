package com.codingPower.framework.worker;

import com.codingPower.framework.net.Response;

/**
 * 网络处理回调接口
 * @author pengfan
 *
 * @param <Response>
 * @param <Values>
 */
public interface NetWorkerHandler {

    /**
     * 网络请求处理
     * @param data
     */
    void handleData(Response rsp);

    /**
     * 处理进度条数据
     */
    void progressUpdate(long current, long allLength);
}
