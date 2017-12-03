/** Created by Jack Chen at 11/13/2014 */
package com.ecnu.trivia.common.exception;

/**
 * 描述异常的状态信息
 *
 * @author Jack Chen
 */
public enum ExceptionStatusValue {
    BAD_REQUEST(400, "Bad Request"),

    UNAUTHORIZED(401, "Unauthorized"),

    PAYMENT_REQUIRED(402, "Payment Required"),

    FORBIDDEN(403, "Forbidden"),

    NOT_FOUND(404, "Not Found"),

    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    NOT_ACCEPTABLE(406, "Not Acceptable"),

    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),

    REQUEST_TIMEOUT(408, "Request Timeout"),

    CONFLICT(409, "Conflict"),

    GONE(410, "Gone"),

    LENGTH_REQUIRED(411, "Length Required"),

    PRECONDITION_FAILED(412, "Precondition Failed"),

    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),

    URI_TOO_LONG(414, "URI Too Long"),

    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"),

    EXPECTATION_FAILED(417, "Expectation Failed"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    NOT_IMPLEMENTED(501, "Not Implemented"),

    BAD_GATEWAY(502, "Bad Gateway"),

    SERVICE_UNAVAILABLE(503, "Service Unavailable"),

    GATEWAY_TIMEOUT(504, "Gateway Timeout"),

    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported"),

    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),

    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),

    LOOP_DETECTED(508, "Loop Detected"),

    BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),

    NOT_EXTENDED(510, "Not Extended"),

    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

    private final int value;

    private final String reasonPhrase;

    private ExceptionStatusValue(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
