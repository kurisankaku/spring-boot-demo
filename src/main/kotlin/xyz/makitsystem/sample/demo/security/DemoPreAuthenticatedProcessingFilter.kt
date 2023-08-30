package xyz.makitsystem.sample.demo.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter

class DemoPreAuthenticatedProcessingFilter: AbstractPreAuthenticatedProcessingFilter() {
    override fun getPreAuthenticatedPrincipal(request: HttpServletRequest?): Any {
        return ""
    }

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest?): Any {
        return ""
    }
}