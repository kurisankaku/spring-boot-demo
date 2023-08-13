package xyz.makitsystem.sample.demo.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.makitsystem.sample.demo.filter.RequestLogFilter

@Configuration
class ApplicationConfiguration {

    @Autowired
    lateinit var requestLogFilter: RequestLogFilter

    @Bean
    fun requestFilter(): FilterRegistrationBean<RequestLogFilter> {
        return FilterRegistrationBean(requestLogFilter).apply {
            addUrlPatterns("/*")
        }
    }
}