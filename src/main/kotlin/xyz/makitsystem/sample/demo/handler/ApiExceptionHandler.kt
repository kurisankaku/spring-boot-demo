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
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import xyz.makitsystem.sample.demo.common.ApiError
import xyz.makitsystem.sample.demo.common.ApiErrorCode
import xyz.makitsystem.sample.demo.common.RequestId
import xyz.makitsystem.sample.demo.exception.ApiException
import java.util.stream.Collectors


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
        ex.bindingResult.fieldErrors.stream().forEach { e: ObjectError ->
            details.add(ApiError.Detail(target = e.objectName, message = getMessage(e, request)))
        }
        val apiError = ApiError(
            code = resolveCode(ex),
            details
        )
        return super.handleExceptionInternal(ex, apiError, headers, status, request)
    }

//    @ExceptionHandler(MethodArgumentNotValidException::class)
//    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String?>>> {
//        val errors = ex.bindingResult.fieldErrors
//            .stream().map { obj: FieldError -> obj.defaultMessage }.collect(Collectors.toList())
//        return ResponseEntity(getErrorsMap(errors), HttpHeaders(), HttpStatus.BAD_REQUEST)
//    }

//    private fun getErrorsMap(errors: List<String?>): Map<String, List<String?>> {
//        val errorResponse: MutableMap<String, List<String?>> = HashMap()
//        errorResponse["errors"] = errors
//        return errorResponse
//    }

    @ExceptionHandler(TransactionSystemException::class)
    fun handleConstraintViolationException(ex: TransactionSystemException): ResponseEntity<ApiError> {
        val exception = ex.cause?.cause
        return if (exception is ConstraintViolationException) {
            val details = exception.constraintViolations.map { item ->
                ApiError.Detail(target = item.propertyPath.toString(), message = item.message)
            }
            val apiError =  ApiError(ApiErrorCode.BAD.value, details)
            log.warn(WARN_LOG_FORMAT, requestId.id, apiError.toString())

            ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
        } else {
            log.error(ERROR_LOG_FORMAT, requestId.id, ex.localizedMessage, ExceptionUtils.getStackTrace(ex))

            ResponseEntity(ApiError(code = ApiErrorCode.INTERNAL_SERVER_ERROR.value, arrayListOf(
                ApiError.Detail(target = "none", message = ex.localizedMessage)
            )), HttpStatus.BAD_REQUEST)
        }
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

//    @ExceptionHandler(NoHandlerFoundException::class)
//    fun handleNoHandlerFoundException(
//        ex: NoHandlerFoundException?
//    ): ResponseEntity<ApiError> {
//        return ResponseEntity(ApiError(
//            code = ApiErrorCode.NOT_FOUND.value,
//            details = arrayListOf(ApiError.Detail(target = "none", message = "Not Found"))
//        ), HttpStatus.NOT_FOUND)
//    }
}