package xyz.makitsystem.sample.demo.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*


enum class OsType(private val value: String) {
    IOS("ios"),
    ANDROID("android");

    @JsonValue
    fun toValue(): String {
        return value
    }

    companion object {
        @JsonCreator
        fun fromValue(value: String): OsType {
            return Arrays.stream(values())
                .filter { v: OsType -> v.value == value }
                .findFirst()
                .orElse(null)
        }
    }
}