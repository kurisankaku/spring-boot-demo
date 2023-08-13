package xyz.makitsystem.sample.demo.exception

import org.springframework.http.HttpStatus
import xyz.makitsystem.sample.demo.common.ApiError

abstract class ApiException(val apiError: ApiError): RuntimeException() {
    abstract val httpStatus: HttpStatus
}