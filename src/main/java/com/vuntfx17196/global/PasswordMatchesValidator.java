package com.vuntfx17196.global;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

	private String password;
	private String passwordConfirm;
	private String message;

	@Override
	public void initialize(final PasswordMatches constraintAnnotation) {
		this.message = constraintAnnotation.message();
		this.password = constraintAnnotation.password();
		this.passwordConfirm = constraintAnnotation.passwordConfirm();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(password);
		Object fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(passwordConfirm);

		boolean isValid = fieldValue != null && fieldValue.equals(fieldMatchValue);

		if (!isValid) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message).addPropertyNode(passwordConfirm)
					.addConstraintViolation();
		}

		return isValid;
	}

}
