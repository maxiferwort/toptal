package com.maxi.nutrition.validator.interfaces;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.maxi.nutrition.validator.validators.NotOtherUserWithUsernameValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = NotOtherUserWithUsernameValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface NotOtherUserWithUsername {

  String message() default "Username already in use";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
