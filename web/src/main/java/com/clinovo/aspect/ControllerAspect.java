package com.clinovo.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);

	@AfterThrowing(pointcut = "execution(* com.clinovo.controller.*.*(..))", throwing = "ex")
	public void afterThrowing(final Throwable ex) throws Throwable {
		LOGGER.error("Error has occurred.", ex);
		throw ex;
	}
}