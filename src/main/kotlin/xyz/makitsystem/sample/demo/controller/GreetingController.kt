package xyz.makitsystem.sample.demo.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.makitsystem.sample.demo.enums.OsType
import java.util.concurrent.atomic.AtomicLong

@RestController
@Validated
class GreetingController {

    val counter = AtomicLong()

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name") @Valid @Email @NotBlank name: String): Greeting {
        println("www")
        return Greeting(counter.incrementAndGet(), name, OsType.ANDROID.toValue())
    }

    @PostMapping("/greeting")
    fun greetingPost(@Valid @RequestBody data: Greeting): Greeting {
        return data
    }

    @GetMapping("/")
    fun root(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting {
        println("root")
        return Greeting(0, "root", OsType.ANDROID.toValue())
    }

    @GetMapping("/greeting/hoge")
    fun greetingHote(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting {
        println("hoge")
        return Greeting(counter.incrementAndGet(), "hoge", OsType.ANDROID.toValue())
    }
}