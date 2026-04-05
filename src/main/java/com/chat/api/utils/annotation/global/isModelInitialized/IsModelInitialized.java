package com.chat.api.utils.annotation.global.isModelInitialized;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ModelInitializedValidator.class)
public @interface IsModelInitialized {
    String message() default "The entity model must be initialized with a valid ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}