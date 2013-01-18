package com.codingPower.framework.net;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.codingPower.framework.util.LogUtil;

/**
 * 针对EMMS接口进行的扩展
 * @author pengfan
 *
 */
public abstract class EMMSBasicService extends BasicService {

    public EMMSBasicService() {
        super();
    }

    public EMMSBasicService(int type) {
        super(type);
    }

    @Override
    public void handleResponse(HttpResponse httpRsp, Response rsp) {
        String str = null;
        try {
            str = EntityUtils.toString(httpRsp.getEntity());
        } catch (Exception e) {
            LogUtil.e(EMMSBasicService.class, e.getMessage());
            rsp.setStatus(RES_FAIL);
            return;
        }
        JSONObject rspObj = isValid(str, rsp);
        if (rspObj != null) {
            handleRspObject(rspObj, rsp);
        }
    }

    public abstract void handleRspObject(JSONObject rspObj, Response rsp);

    /**
     * 首先判断请求是否可用。
     * @return 如果可用  则返回响应结果，以便后续处理，否则返回null
     * 
     */
    protected JSONObject isValid(String res, Response rsp) {
        try {
            if (res == null) {
                return null;
            }
            JSONObject obj = new JSONObject(res);
            String errorCode = "";
            //响应成功
            if ("".equals(errorCode = obj.getString("error_code"))) {
                rsp.setStatus(RES_OK);
                return obj.getJSONObject("response");
            } else {
                rsp.setStatus(RES_FAIL);
                rsp.setErrorCode(errorCode);
                rsp.setErrorMsg(obj.getString("error_msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getString(JSONObject obj, String name) {
        try {
            return obj.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }

    protected String getString(JSONObject obj, String name, String defaultValue) {
        if (obj.isNull(name)) {
            return defaultValue;
        }
        try {
            return obj.getString(name);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    protected int getInteger(JSONObject obj, String name, int defaultValue) {
        try {
            return obj.getInt(name);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    protected double getDouble(JSONObject obj, String name, double defaultValue) {
        try {
            return obj.getDouble(name);
        } catch (JSONException e) {
            return defaultValue;
        }
    }
}
