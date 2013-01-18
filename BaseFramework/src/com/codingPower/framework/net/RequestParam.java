package com.codingPower.framework.net;

import java.io.File;

/**
 * 请求参数接口
 * @author pengfan
 *
 */
public interface RequestParam {

    public void putString(String name, String value);

    public void putFile(String name, File file);

    public String getString(String name);

    public File getFile(String name);
}
