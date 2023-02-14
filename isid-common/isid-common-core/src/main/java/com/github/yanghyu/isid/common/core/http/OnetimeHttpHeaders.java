package com.github.yanghyu.isid.common.core.http;

/**
 * 单次有效HTTP头
 * 规定格式为以'isid-o-'开头
 */
public interface OnetimeHttpHeaders {

    /**
     * 前缀
     */
    String ONETIME_HTTP_HEADER_PREFIX = "isid-o-";

    /**
     * 是否包装后结果
     * 0:未包装
     * 1:已包装
     */
    String PACKAGED_RESULT = ONETIME_HTTP_HEADER_PREFIX + "packaged-result";

    /**
     * 结果异常类型
     * none:无异常
     * sys-ex:系统异常
     * biz-ex:业务异常
     */
    String RESULT_EXCEPTION_TYPE = ONETIME_HTTP_HEADER_PREFIX + "result-ex-type";

    interface ResultExceptionType {
        String NONE = "none";
        String SYS_EXCEPTION = "sys-ex";
        String BIZ_EXCEPTION = "biz-ex";
    }

    /**
     * 请求客户端类型
     * unknown:未知
     * portal-gateway-web:入口WEB网关
     * portal-gateway-api:入口API网关
     * aggregate-service:聚合服务
     * atomic-service:原子服务
     * export-gateway-internet:出口外网网关
     * export-gateway-intranet:出口内网网关
     */
    String REQUEST_CLIENT_TYPE = ONETIME_HTTP_HEADER_PREFIX + "req-client-type";

    interface RequestClientType {
        String UNKNOWN = "unknown";
        String PORTAL_GATEWAY_WEB = "portal-gateway-web";
        String PORTAL_GATEWAY_API = "portal-gateway-api";
        String AGGREGATE_SERVICE = "aggregate-service";
        String ATOMIC_SERVICE = "atomic-service";
        String EXPORT_GATEWAY_INTERNET = "export-gateway-internet";
        String EXPORT_GATEWAY_INTRANET = "export-gateway-intranet";
    }

    /**
     * 请求客户端应用名称
     */
    String REQUEST_CLIENT_APPLICATION_NAME = ONETIME_HTTP_HEADER_PREFIX + "req-client-app-name";

}
