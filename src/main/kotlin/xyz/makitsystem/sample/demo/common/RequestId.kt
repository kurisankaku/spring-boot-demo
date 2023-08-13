package xyz.makitsystem.sample.demo.common

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import java.util.UUID

@RequestScope
@Component
class RequestId {
    val id = UUID.randomUUID().toString()
}