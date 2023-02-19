package com.github.yanghyu.isid.web.client.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetOkHttpClient {

    private static final ResponseCallback DEFAULT_RESPONSE_CALLBACK = new DefaultResponseCallback();

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .build();

    private static final OkHttpClient clientRT1 = client.newBuilder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build();

    /**
     * 连接超时2s
     * 读超时6s
     */
    @Nullable
    public static String newCallExecuteRT6(Request request, ResponseCallback responseCallback) {
        return newCallExecute(client, request, responseCallback);
    }

    /**
     * 连接超时1s
     * 读超时1s
     */
    @Nullable
    public static String newCallExecuteRT1(Request request, ResponseCallback responseCallback) {
        return newCallExecute(clientRT1, request, responseCallback);
    }

    @Nullable
    private static String newCallExecute(OkHttpClient client, Request request, ResponseCallback responseCallback) {
        if (responseCallback == null) {
            responseCallback = DEFAULT_RESPONSE_CALLBACK;
        }
        String rt = "" + client.readTimeoutMillis() / 1000;
        String responseBodyString = null;
        try (Response response = NetOkHttpClient.newCallExecute(client, request)) {
            if (response.isSuccessful()) {
                responseBodyString = responseCallback.successful(response);
                log.info("newCallExecuteRT{} successful responseBodyString:{}", rt, responseBodyString);
            } else {
                log.error("newCallExecuteRT{} unsuccessful responseCode:{}", rt, response.code());
                responseCallback.unsuccessful(response);
            }
        } catch (IOException e) {
            responseCallback.networkError(e);
        }
        return responseBodyString;
    }

    private static Response newCallExecute(OkHttpClient client, Request request) throws IOException {
        String rt = "" + client.readTimeoutMillis() / 1000;
        try {
            log.info("newCallExecuteRT{} {}", rt, request);
            Response response = client.newCall(request).execute();
            log.info("newCallExecuteRT{} {}", rt, response);
            return response;
        } catch (IOException e) {
            log.error("newCallExecuteRT{} IOException", rt, e);
            throw e;
        }
    }

}
