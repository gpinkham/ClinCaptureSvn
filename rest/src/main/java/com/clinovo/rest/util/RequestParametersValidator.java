package com.clinovo.rest.util;

import com.clinovo.rest.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * RequestParametersValidator.
 */
@SuppressWarnings("unused")
public final class RequestParametersValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestParametersValidator.class);

	public static final String REST = "rest.";
	public static final String DOT = ".";
	public static final String EMPTY = ".empty.";
	public static final String MISSING = ".missing.";

	private RequestParametersValidator() {
	}

	/**
	 * Method that validates request parameters.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param messageSource
	 *            MessageSource
	 * @param handler
	 *            HandlerMethod
	 * @throws RestException
	 *             the RestException
	 */
	public static void validate(HttpServletRequest request, MessageSource messageSource, HandlerMethod handler)
			throws RestException {
		Annotation[][] annotationsHolder = handler.getMethod().getParameterAnnotations();
		if (annotationsHolder != null) {
			for (Annotation[] annotations : annotationsHolder) {
				if (annotations != null) {
					for (Annotation annotation : annotations) {
						if (annotation != null && annotation instanceof RequestParam) {
							String parameterName = ((RequestParam) annotation).value();
							if (parameterName != null) {
								if (request.getParameter(parameterName) == null) {
									throw new RestException(messageSource, REST
											.concat(handler.getBean().getClass().getSimpleName().toLowerCase())
											.concat(DOT).concat(handler.getMethod().getName().toLowerCase())
											.concat(MISSING).concat(parameterName.toLowerCase()),
											HttpServletResponse.SC_BAD_REQUEST);
								} else if (request.getParameter(parameterName).trim().isEmpty()) {
									throw new RestException(messageSource, REST
											.concat(handler.getBean().getClass().getSimpleName().toLowerCase())
											.concat(DOT).concat(handler.getMethod().getName().toLowerCase())
											.concat(EMPTY).concat(parameterName.toLowerCase()),
											HttpServletResponse.SC_BAD_REQUEST);
								}
							}
						}
					}
				}
			}
		}
	}
}
