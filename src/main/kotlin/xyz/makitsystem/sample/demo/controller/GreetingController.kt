package xyz.makitsystem.sample.demo.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class GreetingController {

    val counter = AtomicLong()

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name") @Valid @Email @NotBlank name: String): Greeting {
        println("www")
        return Greeting(counter.incrementAndGet(), name)
    }

    @GetMapping("/")
    fun root(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting {
        println("root")
        return Greeting(0, "root")
    }

    @GetMapping("/greeting/hoge")
    fun greetingHote(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting {
        println("hoge")
        return Greeting(counter.incrementAndGet(), "hoge")
    }
}