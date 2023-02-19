package com.github.yanghyu.isid.web.client.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * 响应回调
 */
@Slf4j
public class DefaultResponseCallback implements ResponseCallback {

    /**
     * 成功响应回调（响应码位于200-299之间）
     *
     * @param response  响应
     * @return          响应体转换成的对应字符串
     */
    public String successful(Response response) throws IOException {
        return getResponseBodyString(response);
    }

    /**
     * 非成功响应回调（响应码不位于200-299之间）
     *
     * @param response  响应
     */
    public void unsuccessful(Response response) throws IOException {
        String responseBodyString = getResponseBodyString(response);
        log.error("DefaultResponseCallback unsuccessful responseBodyString:{}", responseBodyString);
        throw new RuntimeException("The system is busy.");
    }

    /**
     * 网络异常回调（网络不通，网络发送失败）
     *
     * @param e 网络异常
     */
    public void networkError(IOException e) {
        throw new RuntimeException("The system is busy.");
    }

    @Nullable
    private String getResponseBodyString(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        String responseBodyString = null;
        if (responseBody != null) {
            responseBodyString = responseBody.string();
        }
        return responseBodyString;
    }

}
