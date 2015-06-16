/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.annotation.RestParameterPossibleValues;
import com.clinovo.rest.annotation.RestParametersPossibleValues;
import com.clinovo.rest.annotation.RestScope;
import com.clinovo.rest.enums.Scope;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.wrapper.RestRequestWrapper;

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
	 * @param dataSource
	 *            DataSource
	 * @param messageSource
	 *            MessageSource
	 * @param handler
	 *            HandlerMethod
	 * @throws RestException
	 *             the RestException
	 */
	public static void validate(HttpServletRequest request, DataSource dataSource, MessageSource messageSource,
			HandlerMethod handler) throws RestException {
		Annotation[][] annotationsHolder = handler.getMethod().getParameterAnnotations();
		if (annotationsHolder != null) {
			for (Annotation[] annotations : annotationsHolder) {
				if (annotations != null) {
					for (Annotation annotation : annotations) {
						if (annotation != null && annotation instanceof RequestParam) {
							String parameterName = ((RequestParam) annotation).value();
							if (parameterName != null) {
								if (((RequestParam) annotation).required()) {
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
								} else {
									if (request.getParameter(parameterName) == null) {
										((RestRequestWrapper) request).addParameter(parameterName,
												((RequestParam) annotation).defaultValue());
									}
								}
							}
						}
					}
				}
			}
		}
		Annotation[] annotationArray = handler.getMethod().getAnnotations();
		if (annotationArray != null) {
			for (Annotation annotation : annotationArray) {
				if (annotation instanceof RestScope && UserDetails.getCurrentUserDetails() != null) {
					StudyBean studyBean = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
					if ((((RestScope) annotation).value() == Scope.STUDY && studyBean.getParentStudyId() > 0)
							|| (((RestScope) annotation).value() == Scope.SITE && studyBean.getParentStudyId() == 0)) {
						throw new RestException(messageSource.getMessage("rest.wrongScope", null,
								LocaleResolver.getLocale()));
					}
				} else if (annotation instanceof RestParametersPossibleValues) {
					for (RestParameterPossibleValues restParameterPossibleValues : ((RestParametersPossibleValues) annotation)
							.value()) {
						String parameterName = restParameterPossibleValues.name();
						String parameterValue = request.getParameter(parameterName);
						if (restParameterPossibleValues.canBeNotSpecified() && parameterValue != null
								&& parameterValue.isEmpty()) {
							continue;
						}
						if (parameterValue != null && restParameterPossibleValues.multiValue() ? !Arrays.asList(
								restParameterPossibleValues.values().split(",")).containsAll(
								Arrays.asList(parameterValue.split(","))) : !Arrays.asList(
								restParameterPossibleValues.values().split(",")).contains(parameterValue)) {
							throw new RestException(messageSource, restParameterPossibleValues.valueDescriptions()
									.isEmpty() ? "rest.possibleValuesAre" : "rest.possibleValuesWithDescriptionsAre",
									restParameterPossibleValues.valueDescriptions().isEmpty() ? new Object[]{
											parameterName, restParameterPossibleValues.values()} : new Object[]{
											parameterName, restParameterPossibleValues.values(),
											restParameterPossibleValues.valueDescriptions()},
									HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						}
					}
				}
			}
		}
	}
}
