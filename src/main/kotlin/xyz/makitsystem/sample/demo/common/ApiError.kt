package xyz.makitsystem.sample.demo.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper

data class ApiError(var code: String, var details: List<Detail>) {
    data class Detail(val target: String, @JsonInclude(JsonInclude.Include.ALWAYS) val message: String)

    override fun toString(): String {
        return try {
            ObjectMapper().writeValueAsString(this)
        } catch (e: JsonParseException) {
            "{ \"code\": \"parseError\", \"message\": \"${e.message}\"}"
        }
    }
}