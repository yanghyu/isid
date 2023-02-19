package com.github.yanghyu.isid.web.client.okhttp;

import okhttp3.Response;

import java.io.IOException;

/**
 * 响应回调
 */
public interface ResponseCallback {

    /**
     * 成功响应回调（响应码位于200-299之间）
     *
     * @param response  响应
     * @return          响应体转换成的对应字符串
     */
    String successful(Response response) throws IOException;

    /**
     * 非成功响应回调（响应码不位于200-299之间）
     *
     * @param response  响应
     */
    void unsuccessful(Response response) throws IOException;

    /**
     * 网络异常回调（网络不通，网络发送失败）
     *
     * @param e 网络异常
     */
    void networkError(IOException e);

}
