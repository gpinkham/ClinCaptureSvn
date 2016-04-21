/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.rest.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.dao.core.CoreResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;

import com.clinovo.enums.BaseEnum;
import com.clinovo.enums.ParameterType;
import com.clinovo.rest.annotation.EnumBasedParameters;
import com.clinovo.rest.annotation.EnumBasedParametersHolder;
import com.clinovo.rest.annotation.PossibleValues;
import com.clinovo.rest.annotation.PossibleValuesHolder;
import com.clinovo.rest.annotation.ProvideAtLeastOneNotRequired;
import com.clinovo.rest.annotation.ScopeIsNotRequired;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.security.PermissionChecker;
import com.clinovo.rest.service.AuthenticationService;
import com.clinovo.rest.wrapper.RestRequestWrapper;

/**
 * RequestParametersValidator.
 *
 * Method processes controller's annotations before the request finds an entry point. Method adds new parameters with
 * default values if it's necessary and if parameters were not specified.
 */
@SuppressWarnings({"unused", "unchecked"})
public final class RequestParametersValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestParametersValidator.class);

	public static final String DOT = ".";
	public static final String REST = "rest.";
	public static final String EMPTY = ".empty.";
	public static final String ACCEPT = "Accept";
	public static final String MISSING = ".missing.";

	private RequestParametersValidator() {
	}

	private static void validateClientVersion(HttpServletRequest request, MessageSource messageSource)
			throws Exception {
		String clientVersion = request.getParameter(PermissionChecker.CLIENT_VERSION);
		if (clientVersion == null || clientVersion.trim().isEmpty()) {
			throw new RestException(messageSource, "rest.eachRequestMustHaveVersionParameter",
					new Object[]{PermissionChecker.CLIENT_VERSION}, HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private static void validateScopeRequirement(UserDetails userDetails, MessageSource messageSource,
			HandlerMethod handler) throws Exception {
		if (userDetails != null && userDetails.getStudyName() == null
				&& !(handler.getBean() instanceof AuthenticationService)) {
			boolean scopeIsRequired = true;
			Annotation[] annotationArray = handler.getMethod().getAnnotations();
			if (annotationArray != null) {
				for (Annotation annotation : annotationArray) {
					if (annotation instanceof ScopeIsNotRequired) {
						scopeIsRequired = false;
						break;
					}
				}
			}
			if (scopeIsRequired) {
				throw new RestException(messageSource, "rest.scopeIsRequired");
			}
		}
	}

	private static void validateRestParametersPossibleValues(HttpServletRequest request, MessageSource messageSource,
			Set<String> nullParameters, HandlerMethod handler) throws Exception {
		Annotation[] annotationArray = handler.getMethod().getAnnotations();
		if (annotationArray != null) {
			for (Annotation annotation : annotationArray) {
				if (annotation instanceof PossibleValuesHolder) {
					for (PossibleValues possibleValues : ((PossibleValuesHolder) annotation).value()) {
						String parameterName = possibleValues.name();
						String parameterValue = request.getParameter(parameterName);
						if (possibleValues.canBeNotSpecified()
								&& (parameterValue == null || nullParameters.contains(parameterName))) {
							continue;
						}
						String values = possibleValues.values();
						String dependentOn = possibleValues.dependentOn();
						String valueDescriptions = possibleValues.valueDescriptions();
						if (!dependentOn.isEmpty()) {
							String dependentOnValue = request.getParameter(dependentOn);
							values = messageSource.getMessage(values.replace("{#}", dependentOnValue), null,
									CoreResources.getSystemLocale());
							valueDescriptions = valueDescriptions.replace("{#}", dependentOnValue);
						}
						if (parameterValue != null && possibleValues.multiValue()
								? !Arrays.asList(values.split(","))
										.containsAll(Arrays.asList(parameterValue.split(",")))
								: !Arrays.asList(values.split(",")).contains(parameterValue)) {
							throw new RestException(messageSource,
									valueDescriptions.isEmpty()
											? "rest.possibleValuesAre"
											: "rest.possibleValuesWithDescriptionsAre",
									valueDescriptions.isEmpty()
											? new Object[]{parameterName, values}
											: new Object[]{parameterName, values,
													messageSource.getMessage(valueDescriptions, null,
															CoreResources.getSystemLocale())},
									HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						}
					}
				}
			}
		}
	}

	private static int validateRequestParameters(HttpServletRequest request, MessageSource messageSource,
			HandlerMethod handler, Set<String> declaredFields, Map<String, String> declaredParams,
			int countOfProvidedNotRequiredParameters) throws Exception {
		EnumBasedParametersHolder enumBasedParametersHolder = handler.getMethod()
				.getAnnotation(EnumBasedParametersHolder.class);
		EnumBasedParameters[] enumBasedParameters = enumBasedParametersHolder != null
				? enumBasedParametersHolder.value()
				: null;
		for (String parameterName : declaredParams.values()) {
			if (!parameterName.equals(PermissionChecker.CLIENT_VERSION) && !declaredFields.contains(parameterName)) {
				BaseEnum baseEnum = null;
				countOfProvidedNotRequiredParameters++;
				String parameterValue = request.getParameter(parameterName);
				if (enumBasedParameters != null && enumBasedParameters.length > 0) {
					for (EnumBasedParameters enumBasedParameter : enumBasedParameters) {
						Class<? extends BaseEnum> enumClass = enumBasedParameter.enumClass();
						boolean hasTypo = (Boolean) enumClass.getMethod("hasTypo", String.class)
								.invoke(enumClass.getEnumConstants()[0], parameterName);
						if (hasTypo) {
							throw new RestException(messageSource, "rest.thereIsTypoInTheParameter",
									new Object[]{parameterName}, HttpServletResponse.SC_BAD_REQUEST);
						}
						baseEnum = (BaseEnum) enumClass.getMethod("find", String.class)
								.invoke(enumClass.getEnumConstants()[0], parameterName);
						if (baseEnum != null) {
							break;
						}
					}
				}
				if (baseEnum != null) {
					if (baseEnum.getType() == ParameterType.SELECT
							|| baseEnum.getType() == ParameterType.RADIO) {
						if (!Arrays.asList(baseEnum.getValues()).contains(parameterValue)) {
							throw new RestException(messageSource, "rest.possibleValuesAre",
									new Object[]{parameterName,
											Arrays.asList(baseEnum.getValues()).toString().replaceAll("\\]|\\[", "")},
									HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						}
					}
				} else if (declaredFields.size() > 0) {
					throw new RestException(messageSource, "rest.parameterIsNotSupported", new Object[]{parameterName},
							HttpServletResponse.SC_BAD_REQUEST);
				}
			}
		}
		return countOfProvidedNotRequiredParameters;
	}

	private static void processEnumBasedParameters(HttpServletRequest request, HandlerMethod handler) throws Exception {
		EnumBasedParametersHolder enumBasedParametersHolder = handler.getMethod()
				.getAnnotation(EnumBasedParametersHolder.class);
		EnumBasedParameters[] enumBasedParameters = enumBasedParametersHolder != null
				? enumBasedParametersHolder.value()
				: null;
		if (enumBasedParameters != null && enumBasedParameters.length > 0) {
			for (EnumBasedParameters enumBasedParameter : enumBasedParameters) {
				if (enumBasedParameter.useDefaultValues()) {
					for (BaseEnum baseEnum : enumBasedParameter.enumClass().getEnumConstants()) {
						String parameterName = baseEnum.getName();
						String defaultValue = baseEnum.getDefaultValue();
						if (request.getParameter(parameterName) == null) {
							((RestRequestWrapper) request).addParameter(parameterName, defaultValue);
						}
					}
				}
			}
		}
	}

	private static int validateMethodParameterAnnotations(HttpServletRequest request, MessageSource messageSource,
			HandlerMethod handler, Set<String> declaredFields, Map<String, String> declaredParams,
			Set<String> nullParameters, int countOfProvidedNotRequiredParameters) {
		Annotation[][] annotationsHolder = handler.getMethod().getParameterAnnotations();
		if (annotationsHolder != null) {
			for (Annotation[] annotations : annotationsHolder) {
				if (annotations != null) {
					for (Annotation annotation : annotations) {
						if (annotation != null && annotation instanceof RequestParam) {
							String parameterName = ((RequestParam) annotation).value();
							if (parameterName != null) {
								declaredFields.add(parameterName);
								if (declaredParams.keySet().contains(parameterName.toLowerCase())
										&& !declaredParams.values().contains(parameterName)) {
									throw new RestException(messageSource, "rest.thereIsTypoInTheParameter",
											new Object[]{declaredParams.get(parameterName.toLowerCase())},
											HttpServletResponse.SC_BAD_REQUEST);
								}
								if (((RequestParam) annotation).required()) {
									if (request.getParameter(parameterName) == null) {
										throw new RestException(
												messageSource, REST
														.concat(handler.getBean().getClass().getSimpleName()
																.toLowerCase())
														.concat(DOT).concat(handler.getMethod().getName().toLowerCase())
														.concat(MISSING).concat(parameterName.toLowerCase()),
												HttpServletResponse.SC_BAD_REQUEST);
									} else if (request.getParameter(parameterName).trim().isEmpty()) {
										throw new RestException(
												messageSource, REST
														.concat(handler.getBean().getClass().getSimpleName()
																.toLowerCase())
														.concat(DOT).concat(handler.getMethod().getName().toLowerCase())
														.concat(EMPTY).concat(parameterName.toLowerCase()),
												HttpServletResponse.SC_BAD_REQUEST);
									}
								} else {
									boolean notUsed = ((RequestParam) annotation).defaultValue()
											.equals(ValueConstants.DEFAULT_NONE);
									String parameterValue = request.getParameter(parameterName);
									if (notUsed && parameterValue != null) {
										countOfProvidedNotRequiredParameters++;
									} else if (!notUsed && parameterValue == null) {
										nullParameters.add(parameterName);
										String defaultValue = ((RequestParam) annotation).defaultValue();
										((RestRequestWrapper) request).addParameter(parameterName, defaultValue);
									}
								}
							}
						}
					}
				}
			}
		}
		return countOfProvidedNotRequiredParameters;
	}

	private static void fillDeclaredParams(HttpServletRequest request, Map<String, String> declaredParams) {
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements()) {
			String paramName = enumeration.nextElement();
			declaredParams.put(paramName.toLowerCase(), paramName);
		}
	}

	/**
	 * Method that validates request parameters.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param dataSource
	 *            DataSource
	 * @param messageSource
	 *            MessageSource
	 * @param handler
	 *            HandlerMethod
	 * @throws Exception
	 *             the Exception
	 */
	public static void validate(HttpServletRequest request, DataSource dataSource, MessageSource messageSource,
			HandlerMethod handler) throws Exception {
		int countOfProvidedNotRequiredParameters = 0;
		Set<String> nullParameters = new HashSet<String>();
		Set<String> declaredFields = new HashSet<String>();
		Map<String, String> declaredParams = new HashMap<String, String>();
		UserDetails userDetails = (UserDetails) request.getSession()
				.getAttribute(PermissionChecker.API_AUTHENTICATED_USER_DETAILS);
		if (userDetails == null && !(handler.getBean() instanceof AuthenticationService)) {
			return;
		}
		fillDeclaredParams(request, declaredParams);
		validateClientVersion(request, messageSource);
		processEnumBasedParameters(request, handler);
		countOfProvidedNotRequiredParameters = validateMethodParameterAnnotations(request, messageSource, handler,
				declaredFields, declaredParams, nullParameters, countOfProvidedNotRequiredParameters);
		validateScopeRequirement(userDetails, messageSource, handler);
		validateRestParametersPossibleValues(request, messageSource, nullParameters, handler);
		countOfProvidedNotRequiredParameters = validateRequestParameters(request, messageSource, handler,
				declaredFields, declaredParams, countOfProvidedNotRequiredParameters);
		if (handler.getMethod().getAnnotation(ProvideAtLeastOneNotRequired.class) != null
				&& countOfProvidedNotRequiredParameters == 0) {
			throw new RestException(messageSource, "rest.atLeastOneNotRequiredParameterShouldBeSpecified");
		}
	}
}
