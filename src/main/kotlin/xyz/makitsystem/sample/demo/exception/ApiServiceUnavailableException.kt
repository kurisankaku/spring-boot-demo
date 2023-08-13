package xyz.makitsystem.sample.demo.exception

import org.springframework.http.HttpStatus
import xyz.makitsystem.sample.demo.common.ApiError
import xyz.makitsystem.sample.demo.common.ApiErrorCode

class ApiServiceUnavailableException(details: List<ApiError.Detail>) :
    ApiException(ApiError(ApiErrorCode.INTERNAL_SERVER_ERROR.value, arrayListOf())) {
    init {
        this.apiError.details = details
    }

    override val httpStatus: HttpStatus
        get() = HttpStatus.SERVICE_UNAVAILABLE
}