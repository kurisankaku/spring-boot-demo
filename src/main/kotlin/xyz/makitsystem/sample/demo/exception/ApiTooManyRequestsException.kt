package xyz.makitsystem.sample.demo.exception

import org.springframework.http.HttpStatus
import xyz.makitsystem.sample.demo.common.ApiError
import xyz.makitsystem.sample.demo.common.ApiErrorCode

class ApiTooManyRequestsException(details: List<ApiError.Detail>) :
    ApiException(ApiError(ApiErrorCode.TOO_MANY_REQUESTS.value, arrayListOf())) {
    init {
        this.apiError.details = details
    }

    override val httpStatus: HttpStatus
        get() = HttpStatus.TOO_MANY_REQUESTS
}