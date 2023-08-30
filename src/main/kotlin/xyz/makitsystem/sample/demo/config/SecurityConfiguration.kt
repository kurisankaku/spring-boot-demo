package xyz.makitsystem.sample.demo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import xyz.makitsystem.sample.demo.security.DemoAuthenticationUserDetailsService
import xyz.makitsystem.sample.demo.security.DemoPreAuthenticatedProcessingFilter


@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
        http
            .addFilter(preAuthenticatedProcessingFilter(authenticationManager))
            .authenticationProvider(preAuthenticatedAuthenticationProvider())
            .authorizeHttpRequests{
            it.anyRequest().permitAll()
        }

        return http.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.getAuthenticationManager()
    }

    @Bean
    fun authenticationUserDetailsService(): AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
        return DemoAuthenticationUserDetailsService()
    }

    @Bean
    fun preAuthenticatedAuthenticationProvider(): PreAuthenticatedAuthenticationProvider {
        return PreAuthenticatedAuthenticationProvider().apply {
            setPreAuthenticatedUserDetailsService(authenticationUserDetailsService())
        }
    }

    @Bean
    fun preAuthenticatedProcessingFilter(authenticationManager: AuthenticationManager): AbstractPreAuthenticatedProcessingFilter {
        return DemoPreAuthenticatedProcessingFilter().apply {
            setAuthenticationManager(authenticationManager)
        }
    }
}