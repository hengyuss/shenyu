package org.apache.shenyu.plugin.record.entity;

import java.util.Map;

public class ShenyuHttpRequestRecord {

    private String traceId;

    private String method;

    private String requestUri;

    private String queryParams;

    private String requestBody;

    private Map<String, String> requestHeaders;

    private Integer status;

    private Map<String, String> responseHeaders;

    private String responseBody;


    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
    public String getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Integer getStatus() {
        return status;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

}

