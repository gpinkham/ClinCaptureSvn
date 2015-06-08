package com.clinovo.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Rest parameters possible values annotation.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestParametersPossibleValues {

	/**
	 * Method that returns annotation value.
	 */
	RestParameterPossibleValues[] value();
}
