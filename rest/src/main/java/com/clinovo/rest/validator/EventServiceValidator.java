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

package com.clinovo.rest.validator;

import java.util.Locale;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.springframework.context.MessageSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.wrapper.RestRequestWrapper;
import com.clinovo.util.RequestUtil;

/**
 * EventServiceValidator.
 */
public final class EventServiceValidator {

	private EventServiceValidator() {
	}

	/**
	 * Validates StudyEventDefinitionBean.
	 *
	 * @param messageSource
	 *            MessageSource
	 * @param id
	 *            int
	 * @param studyEventDefinitionBean
	 *            StudyEventDefinitionBean
	 * @param currentStudy
	 *            StudyBean
	 * @param editMode
	 *            boolean
	 * @param userAccountDao
	 *            UserAccountDAO
	 * @throws RestException
	 *             the RestException
	 */
	public static void validateStudyEventDefinition(MessageSource messageSource, int id,
			StudyEventDefinitionBean studyEventDefinitionBean, StudyBean currentStudy, UserAccountDAO userAccountDao,
			boolean editMode) throws RestException {
		Locale locale = LocaleResolver.getLocale();
		if (!(studyEventDefinitionBean.getId() > 0)) {
			throw new RestException(messageSource.getMessage("rest.event.isNotFound", new Object[]{id}, locale));
		} else if (studyEventDefinitionBean.getStudyId() != currentStudy.getId()) {
			throw new RestException(
					messageSource.getMessage("rest.event.studyEventDefinitionDoesNotBelongToCurrentScope",
							new Object[]{id, currentStudy.getId()}, locale));
		}
		if (editMode) {
			prepareForValidation("name", studyEventDefinitionBean.getName());
			prepareForValidation("description", studyEventDefinitionBean.getDescription());
			prepareForValidation("repeating", studyEventDefinitionBean.isRepeating());
			prepareForValidation("category", studyEventDefinitionBean.getCategory());
			if (prepareForValidation("type", studyEventDefinitionBean.getType()).equalsIgnoreCase("calendared_visit")) {
				boolean isRreference = prepareForValidation("isReference", studyEventDefinitionBean.getReferenceVisit())
						.equalsIgnoreCase("true");
				prepareForValidation("schDay", !isRreference ? studyEventDefinitionBean.getScheduleDay() : 0);
				prepareForValidation("maxDay", !isRreference ? studyEventDefinitionBean.getMaxDay() : 0);
				prepareForValidation("minDay", !isRreference ? studyEventDefinitionBean.getMinDay() : 0);
				prepareForValidation("emailDay", !isRreference ? studyEventDefinitionBean.getEmailDay() : 0);
				prepareForValidation("emailUser", !isRreference
						? userAccountDao.findByPK(studyEventDefinitionBean.getUserEmailId()).getName()
						: "");
			}
		}
	}

	/**
	 * Prepares request parameters for validation.
	 * 
	 * @param parameterName
	 *            String
	 * @param objectValue
	 *            String
	 * @return String
	 */
	public static String prepareForValidation(String parameterName, String objectValue) {
		RestRequestWrapper requestWrapper = (RestRequestWrapper) RequestUtil.getRequest();
		String parameterValue = requestWrapper.getParameter(parameterName);
		parameterValue = parameterValue != null ? parameterValue : objectValue;
		requestWrapper.addParameter(parameterName, parameterValue);
		return parameterValue;
	}

	/**
	 * Prepares request parameters for validation.
	 *
	 * @param parameterName
	 *            String
	 * @param objectValue
	 *            Boolean
	 * @return String
	 */
	public static String prepareForValidation(String parameterName, Boolean objectValue) {
		RestRequestWrapper requestWrapper = (RestRequestWrapper) RequestUtil.getRequest();
		String parameterValue = requestWrapper.getParameter(parameterName);
		parameterValue = parameterValue != null ? parameterValue : objectValue.toString();
		requestWrapper.addParameter(parameterName, parameterValue);
		return parameterValue;
	}

	/**
	 * Prepares request parameters for validation.
	 *
	 * @param parameterName
	 *            String
	 * @param objectValue
	 *            Integer
	 * @return String
	 */
	public static String prepareForValidation(String parameterName, Integer objectValue) {
		RestRequestWrapper requestWrapper = (RestRequestWrapper) RequestUtil.getRequest();
		String parameterValue = requestWrapper.getParameter(parameterName);
		parameterValue = parameterValue != null ? parameterValue : objectValue.toString();
		requestWrapper.addParameter(parameterName, parameterValue);
		return parameterValue;
	}
}
