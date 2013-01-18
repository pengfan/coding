package com.codingPower.framework.worker;

import com.codingPower.framework.net.Response;

public interface FileNetWorkerHandler {
    void handleData(Response rsp);

    void progressUpdate(Object[] vals);

}
