package xyz.makitsystem.sample.demo.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import xyz.makitsystem.sample.demo.common.RequestId

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestLogFilter : OncePerRequestFilter() {
    companion object {
        const val REQUEST_LOG_FORMAT = "RequestId:{}\tTYPE:Request\tMETHOD:{}\tURI:{}\tIP:{}\tHEADER:{}"
        const val RESPONSE_LOG_FORMAT = "RequestId:{}\tTYPE:Response\tMETHOD:{}\tURI:{}\tSTATUS:{}\tIP:{}";
    }

    val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var requestId: RequestId

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val id = requestId.id
        val method = request.method
        val url = buildUriWithParams(request)
        val addr = request.remoteAddr
        val headers = generateRequestHeaderMaps(request)

        log.info(
            REQUEST_LOG_FORMAT,
            id,
            method,
            url,
            addr,
            headers
        )
        filterChain.doFilter(request, response)
        log.info(RESPONSE_LOG_FORMAT, id, method, url, response.status, addr)
    }

    fun buildUriWithParams(request: HttpServletRequest): String {
        var uri = request.requestURI
        val queryString = request.queryString
        if (queryString != null) {
            uri = "$uri?$queryString"
        }
        return uri
    }

    fun generateRequestHeaderMaps(request: HttpServletRequest): String {
        val header = request.headerNames
        val builder = StringBuilder()
        while (header.hasMoreElements()) {
            val name = header.nextElement() as String
            val value = request.getHeader(name)
            builder.append(name)
            builder.append("=\"")
            builder.append(value)
            builder.append("\" ")
        }
        return builder.toString()
    }
}