package com.vuntfx17196.global;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
	String message() default "Passwords don't match";

	String password();

	String passwordConfirm();

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Target({ TYPE })
	@Retention(RUNTIME)
	@interface List {
		PasswordMatches[] value();
	}
}
