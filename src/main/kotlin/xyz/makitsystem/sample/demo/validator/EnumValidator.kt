package xyz.makitsystem.sample.demo.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [EnumValidatorImpl::class])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.TYPE, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
@ReportAsSingleViolation
annotation class EnumValidator(
    val enumClazz: KClass<out Enum<*>>,
    val message: String = "{constraints.EnumValidator.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)