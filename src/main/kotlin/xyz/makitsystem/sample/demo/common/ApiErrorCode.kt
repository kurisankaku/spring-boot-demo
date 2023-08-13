package xyz.makitsystem.sample.demo.common

enum class ApiErrorCode(val value: String) {
    BAD("badRequest"),
    FORBIDDEN("forbidden"),
    GATEWAY_TIMEOUT("gatewayTimeout"),
    NOT_ACCEPTABLE("notAcceptable"),
    NOT_FOUND("notFound"),
    SERVICE_UNAVAILABLE("serviceUnavailable"),
    INTERNAL_SERVER_ERROR("internalServerError"),
    TOO_MANY_REQUESTS("tooManyRequests"),
    UNAUTHORIZED("unauthorized")
}