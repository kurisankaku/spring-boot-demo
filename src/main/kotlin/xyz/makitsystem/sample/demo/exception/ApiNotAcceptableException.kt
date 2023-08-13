package xyz.makitsystem.sample.demo.exception

import org.springframework.http.HttpStatus
import xyz.makitsystem.sample.demo.common.ApiError
import xyz.makitsystem.sample.demo.common.ApiErrorCode

class ApiNotAcceptableException(details: List<ApiError.Detail>) :
    ApiException(ApiError(ApiErrorCode.NOT_ACCEPTABLE.value, arrayListOf())) {
    init {
        this.apiError.details = details
    }

    override val httpStatus: HttpStatus
        get() = HttpStatus.NOT_ACCEPTABLE
}