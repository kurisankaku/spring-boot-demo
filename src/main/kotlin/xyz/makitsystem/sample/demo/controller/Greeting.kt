package xyz.makitsystem.sample.demo.controller

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import xyz.makitsystem.sample.demo.enums.OsType
import xyz.makitsystem.sample.demo.validator.EnumValidator

data class Greeting(
    @field:Min(2)
    var id: Long,
    @field:NotBlank
    var content: String,
    @field:EnumValidator(enumClazz = OsType::class)
    var os: String,
)