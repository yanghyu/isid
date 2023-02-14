package com.github.yanghyu.isid.common.core.http;

/**
 * 可透传的HTTP头
 * 规定格式为以'isid-t-'开头
 */
public interface TransparentHttpHeaders {

    /**
     * 前缀
     */
    String TRANSPARENT_HTTP_HEADER_PREFIX = "isid-t-";

    /**
     * 集群编号
     */
    String CLUSTER = TRANSPARENT_HTTP_HEADER_PREFIX + "cluster-id";

    /**
     * 路由标记
     */
    String ROUTE_TAGS = TRANSPARENT_HTTP_HEADER_PREFIX + "route-tags";

}
