package xyz.makitsystem.sample.demo.handler

import jakarta.validation.ConstraintViolationException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import xyz.makitsystem.sample.demo.common.ApiError
import xyz.makitsystem.sample.demo.common.ApiErrorCode
import xyz.makitsystem.sample.demo.common.RequestId
import xyz.makitsystem.sample.demo.exception.ApiException

@ControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    companion object {
        private const val ERROR_LOG_FORMAT = "REQUEST_ID:{}\tMESSAGE:{}\tSTACKTRACE:{}"
        private const val WARN_LOG_FORMAT = "REQUEST_ID:{}\tMESSAGE:{}"
    }

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var message: MessageSource

    @Autowired
    lateinit var requestId: RequestId

    private fun resolveCode(ex: Exception): String {
        return when {
            MethodArgumentNotValidException::class.java.isAssignableFrom(ex.javaClass) -> ApiErrorCode.BAD.value
            BindException::class.java.isAssignableFrom(ex.javaClass) -> ApiErrorCode.BAD.value
            else -> ApiErrorCode.INTERNAL_SERVER_ERROR.value
        }
    }

    private fun getMessage(resolvable: MessageSourceResolvable, request: WebRequest): String {
        return message.getMessage(resolvable, request.locale)
    }

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.error(ERROR_LOG_FORMAT, requestId.id, ex.localizedMessage, ExceptionUtils.getStackTrace(ex))
        val apiError = ApiError(
            code = resolveCode(ex),
            arrayListOf(ApiError.Detail(target = "none", message = ex.localizedMessage))
        )
        return super.handleExceptionInternal(ex, apiError, headers, statusCode, request)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val details = mutableListOf<ApiError.Detail>()
        ex.bindingResult.globalErrors.stream().forEach { e: ObjectError ->
            details.add(ApiError.Detail(target = e.objectName, message = getMessage(e, request)))
        }
        ex.bindingResult.fieldErrors.stream().forEach { e: FieldError ->
            details.add(ApiError.Detail(target = e.field, message = getMessage(e, request)))
        }
        val apiError = ApiError(
            code = resolveCode(ex),
            details
        )
        return super.handleExceptionInternal(ex, apiError, headers, status, request)
    }

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val hoge = "a"
        return super.handleNoHandlerFoundException(ex, headers, status, request)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<ApiError> {
        val details = ex.constraintViolations.map { item ->
            ApiError.Detail(target = item.propertyPath.last().toString(), message = item.message)
        }
        val apiError = ApiError(ApiErrorCode.BAD.value, details)
        log.warn(WARN_LOG_FORMAT, requestId.id, apiError.toString())

        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleServerException(exception: Exception): ResponseEntity<ApiError> {
        log.error(
            ERROR_LOG_FORMAT,
            requestId.id,
            exception.localizedMessage,
            ExceptionUtils.getStackTrace(exception)
        )
        return ResponseEntity(
            ApiError(
                code = ApiErrorCode.INTERNAL_SERVER_ERROR.value,
                details = arrayListOf(ApiError.Detail(target = "none", message = exception.localizedMessage))
            ), HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(exception: ApiException, request: WebRequest?): ResponseEntity<ApiError> {
        log.warn(WARN_LOG_FORMAT, requestId.id, exception.apiError.toString())

        return ResponseEntity(exception.apiError, exception.httpStatus)
    }
}