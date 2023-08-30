package xyz.makitsystem.sample.demo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

import org.springframework.security.web.util.matcher.RequestMatcher




class DemoPreAuthenticatedProcessingFilter: AbstractPreAuthenticatedProcessingFilter() {

    var customFilterUrl: RequestMatcher = AntPathRequestMatcher("/greeting/**")
    var errorFilterUrl: RequestMatcher = AntPathRequestMatcher("/error/**")

    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest?): Any {
        return ""
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest?): Any {
        return ""
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (customFilterUrl.matches(request as HttpServletRequest) || errorFilterUrl.matches(request as HttpServletRequest)) {
            chain?.doFilter(request, response)
        } else {
            super.doFilter(request, response, chain)
        }
    }
}