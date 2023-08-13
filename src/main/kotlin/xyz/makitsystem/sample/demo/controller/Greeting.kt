package xyz.makitsystem.sample.demo.controller

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class Greeting(
    @field:Min(2)
    var id: Long,
    @field:NotBlank
    var content: String)