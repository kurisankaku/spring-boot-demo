package xyz.makitsystem.sample.demo.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class EnumValidatorImpl : ConstraintValidator<EnumValidator, Any?> {
    private var valueList: MutableList<String> = mutableListOf()
    private val errorMessage = "There is no toValue() method on the given Enum. Please implement the getValue() method."

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        return !(value != null && !valueList.contains(value.toString()))
    }

    override fun initialize(constraintAnnotation: EnumValidator) {
        val enumClass: Class<out Enum<*>> = constraintAnnotation.enumClazz.java
        val enumValArr: Array<out Enum<*>> = enumClass.getEnumConstants()
        try {
            for (enumVal in enumValArr) {
                val method: Method = enumClass.getMethod("toValue")
                valueList.add(java.lang.String.valueOf(method.invoke(enumVal)))
            }
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(
                errorMessage,
                e.cause
            )
        } catch (e: SecurityException) {
            throw RuntimeException(
                errorMessage,
                e.cause
            )
        } catch (e: IllegalArgumentException) {
            throw RuntimeException(
                errorMessage,
                e.cause
            )
        } catch (e: IllegalAccessException) {
            throw RuntimeException(
                errorMessage,
                e.cause
            )
        } catch (e: InvocationTargetException) {
            throw RuntimeException(
                errorMessage,
                e.cause
            )
        }
    }
}