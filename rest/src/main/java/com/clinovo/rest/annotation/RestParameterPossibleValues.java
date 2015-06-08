package com.clinovo.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Rest parameter possible values annotation.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestParameterPossibleValues {

	/**
	 * Method that returns name.
	 */
	String name();

	/**
	 * Method that returns values.
	 */
	String values();

	/**
	 * Method that returns value descriptions.
	 */
	String valueDescriptions() default "";

	/**
	 * Method that returns multiValue.
	 */
	boolean multiValue() default false;

	/**
	 * Method that returns canBeNotSpecified.
	 */
	boolean canBeNotSpecified() default false;
}
