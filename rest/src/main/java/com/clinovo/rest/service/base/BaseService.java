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
package com.clinovo.rest.service.base;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.service.StudyConfigService;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.wrapper.RestRequestWrapper;
import com.clinovo.util.RequestUtil;

/**
 * BaseService.
 */
public abstract class BaseService {

	public static final int PROPAGATE_CHANGE_NO = 3;

	public static final String YES = "yes";
	public static final String NAME = "name";
	public static final String UTF_8 = "UTF-8";
	public static final String XS_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	@Autowired
	private StudyConfigService studyConfigService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DataSource dataSource;

	protected UserDetails getUserDetails() {
		return UserDetails.getCurrentUserDetails();
	}

	protected StudyBean getCurrentStudy() {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		studyConfigService.setParametersForStudy(currentStudy);
		return currentStudy;
	}

	protected UserAccountBean getCurrentUser() {
		return UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
	}

	protected static String prepareForValidation(String parameterName, String objectValue) {
		RestRequestWrapper requestWrapper = (RestRequestWrapper) RequestUtil.getRequest();
		String parameterValue = requestWrapper.getParameter(parameterName);
		parameterValue = parameterValue != null ? parameterValue : objectValue;
		requestWrapper.addParameter(parameterName, parameterValue);
		return parameterValue;
	}

	protected static String prepareForValidation(String parameterName, Boolean objectValue) {
		return prepareForValidation(parameterName, objectValue.toString());
	}

	protected static String prepareForValidation(String parameterName, Integer objectValue) {
		return prepareForValidation(parameterName, objectValue.toString());
	}

	protected StudyBean getSite(String siteName) throws Exception {
		StudyBean site = (StudyBean) getStudyDAO().findByName(siteName);
		if (site.getId() == 0) {
			throw new RestException(messageSource, "rest.eventservice.editsitecrf.siteIsNotFound",
					new Object[]{siteName}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (site.getParentStudyId() != getCurrentStudy().getId()) {
			throw new RestException(messageSource, "rest.eventservice.editsitecrf.siteDoesNotBelongToCurrentScope",
					new Object[]{siteName}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		studyConfigService.setParametersForSite(site);
		return site;
	}

	protected List<String> commaSeparatedStringAsList(String values) {
		List<String> list = new ArrayList<String>();
		if (values != null && !values.isEmpty()) {
			for (String versionId : values.split(",")) {
				versionId = versionId.trim();
				if (!list.contains(versionId)) {
					list.add(versionId);
				}
			}
		}
		return list;
	}

	protected String listAsCommaSeparatedString(List<String> stringList) {
		String result = "";
		if (stringList.size() > 0) {
			String listAsString = stringList.toString();
			result = listAsString.substring(1, listAsString.length() - 1);
		}
		return result.replaceAll(" ", "");
	}

	protected String intArrayAsString(Integer[] intArray) {
		String result = "";
		for (Integer value : intArray) {
			result += (result.isEmpty() ? "" : ",").concat(Integer.toString(value));
		}
		return result;
	}

	protected CRFDAO getCRFDAO() {
		return new CRFDAO(dataSource);
	}

	protected StudyDAO getStudyDAO() {
		return new StudyDAO(dataSource);
	}

	protected CRFVersionDAO getCRFVersionDAO() {
		return new CRFVersionDAO(dataSource);
	}

	protected UserAccountDAO getUserAccountDAO() {
		return new UserAccountDAO(dataSource);
	}

	protected EventDefinitionCRFDAO getEventDefinitionCRFDAO() {
		return new EventDefinitionCRFDAO(dataSource);
	}

	protected StudyEventDefinitionDAO getStudyEventDefinitionDAO() {
		return new StudyEventDefinitionDAO(dataSource);
	}
}
