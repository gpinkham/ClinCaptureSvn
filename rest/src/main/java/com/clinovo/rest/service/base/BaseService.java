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

import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.namespace.QName;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.json.JSONObject;
import org.jvnet.ws.wadl.Resource;
import org.jvnet.ws.wadl.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.ValidatorUtil;

/**
 * BaseService.
 */
@SuppressWarnings("unchecked")
public abstract class BaseService {

	public static final String NAME = "name";
	public static final String UTF_8 = "UTF-8";
	public static final int PROPAGATE_CHANGE_NO = 3;
	public static final String STATUS_OK = "Status=OK";
	public static final String XS_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DataSource dataSource;

	protected StudyBean getCurrentStudy() {
		return UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
	}

	protected UserAccountBean getCurrentUser() {
		return UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
	}

	protected QName convertJavaToXMLType(Class<?> type) {
		QName nm = new QName("");
		String className = type.toString();
		if (className.contains("String")) {
			nm = new QName(XS_NAMESPACE, "string", "xs");
		} else if (className.contains("Integer")) {
			nm = new QName(XS_NAMESPACE, "integer", "xs");
		} else if (className.contains("int")) {
			nm = new QName(XS_NAMESPACE, "int", "xs");
		} else if (className.contains("boolean") || className.contains("Boolean")) {
			nm = new QName(XS_NAMESPACE, "boolean", "xs");
		}
		return nm;
	}

	protected Resource createOrFind(String uri, Resources wadResources) {
		List<Resource> current = wadResources.getResource();
		for (Resource resource : current) {
			if (resource.getPath().equalsIgnoreCase(uri)) {
				return resource;
			}
		}
		Resource wadlResource = new Resource();
		current.add(wadlResource);
		return wadlResource;
	}

	protected String getBaseUrl(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return request.getScheme().concat("://").concat(request.getServerName()).concat(":")
				.concat(Integer.toString(request.getServerPort())).concat(requestUri.replaceAll("/wadl.*", ""));
	}

	protected String cleanDefault(String value) {
		value = value.replaceAll("\t", "");
		value = value.replaceAll("\n", "");
		return value;
	}

	protected CRFVersionBean importCrf(String jsonData, boolean importCrfVersion) throws Exception {
		StudyBean currentStudy = UserDetails.getCurrentUserDetails().getCurrentStudy(dataSource);
		UserAccountBean owner = UserDetails.getCurrentUserDetails().getCurrentUser(dataSource);
		CrfBuilder crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, currentStudy, owner,
				LocaleResolver.getLocale(), messageSource);
		if (importCrfVersion) {
			crfBuilder.build(getCrfBean(jsonData).getId());
		} else {
			crfBuilder.build();
		}
		return save(crfBuilder, importCrfVersion);
	}

	protected CRFBean getCrfBean(String jsonData) throws Exception {
		JSONObject jsonObject = new JSONObject(jsonData);
		String crfName = URLDecoder.decode(jsonObject.getString(NAME), UTF_8).trim();
		if (crfName.isEmpty()) {
			throw new RestException(messageSource, "rest.crf.crfNameIsEmpty");
		}
		CRFBean crfBean = (CRFBean) new CRFDAO(dataSource).findByName(crfName);
		if (crfBean.getId() == 0) {
			throw new RestException(messageSource, "rest.crf.crfNameDoesNotExist", new Object[]{crfName},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return crfBean;
	}

	protected CRFVersionBean save(CrfBuilder crfBuilder, boolean importCrfVersion) throws Exception {
		ValidatorUtil.checkForErrors(crfBuilder.getErrorsList());
		CRFVersionBean crfVersionBean = crfBuilder.save();
		if (crfVersionBean.getId() == 0) {
			throw new RestException(messageSource,
					importCrfVersion ? "rest.importCrfVersion.operationFailed" : "rest.importCrf.operationFailed");
		}
		return crfVersionBean;
	}

	protected UserAccountBean getUserAccountBean(String userName) {
		UserAccountDAO userAccountDAO = new UserAccountDAO(dataSource);
		UserAccountBean userAccountBean = (UserAccountBean) userAccountDAO.findByUserName(userName);
		if (userName.equals("root")) {
			throw new RestException(messageSource, "rest.userAPI.itIsForbiddenToPerformThisOperationOnRootUser",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (userAccountBean.getId() == 0) {
			throw new RestException(messageSource, "rest.userAPI.userDoesNotExist",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (userAccountBean.getId() == UserDetails.getCurrentUserDetails().getUserId()) {
			throw new RestException(messageSource, "rest.userAPI.itIsForbiddenToPerformThisOperationOnYourself",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (!UserDetails.getCurrentUserDetails().getRoleCode().equals(Role.SYSTEM_ADMINISTRATOR.getCode())) {
			boolean allowToProceed = false;
			List<StudyUserRoleBean> studyUserRoleBeanList = (List<StudyUserRoleBean>) userAccountDAO
					.findAllRolesByUserName(UserDetails.getCurrentUserDetails().getUserName());
			for (StudyUserRoleBean studyUserRoleBean : studyUserRoleBeanList) {
				if (userAccountDAO.isUserPresentInStudy(userName, studyUserRoleBean.getStudyId())) {
					allowToProceed = true;
					break;
				}
			}
			if (!allowToProceed) {
				throw new RestException(messageSource,
						"rest.userAPI.itIsForbiddenToPerformThisOperationOnUserThatDoesNotBelongToCurrentUserScope",
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		userAccountBean.setUserTypeCode(
				userAccountBean.hasUserType(UserType.SYSADMIN) ? UserType.SYSADMIN.getCode() : UserType.USER.getCode());
		userAccountBean.setPasswd("");
		return userAccountBean;
	}
}
