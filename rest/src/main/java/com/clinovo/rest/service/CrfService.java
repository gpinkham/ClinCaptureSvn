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

package com.clinovo.rest.service;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;
import com.clinovo.rest.annotation.RestAccess;
import com.clinovo.rest.annotation.RestScope;
import com.clinovo.rest.enums.Scope;
import com.clinovo.rest.enums.UserRole;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.model.UserDetails;
import com.clinovo.rest.util.ValidatorUtil;

/**
 * CrfService.
 */
@Controller("restCrfService")
@RequestMapping("/crf")
public class CrfService {

	public static final String NAME = "name";
	public static final String UTF_8 = "UTF-8";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	/**
	 * Method imports new crf.
	 *
	 * @param jsonData
	 *            String
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RestAccess({UserRole.SYS_ADMIN, UserRole.STUDY_ADMIN_USER, UserRole.STUDY_ADMIN_ADMIN})
	@ResponseBody
	@RestScope(Scope.STUDY)
	@RequestMapping(value = "/json/importCrf", method = RequestMethod.POST)
	public CRFVersionBean importCrf(@RequestParam("jsondata") String jsonData) throws Exception {
		return processRequest(jsonData, false);
	}

	/**
	 * Method imports new crf version.
	 *
	 * @param jsonData
	 *            String
	 * @return CRFVersionBean
	 * @throws Exception
	 *             an Exception
	 */
	@RestAccess({UserRole.SYS_ADMIN, UserRole.STUDY_ADMIN_USER, UserRole.STUDY_ADMIN_ADMIN})
	@ResponseBody
	@RestScope(Scope.STUDY)
	@RequestMapping(value = "/json/importCrfVersion", method = RequestMethod.POST)
	public CRFVersionBean importCrfVersion(@RequestParam("jsondata") String jsonData) throws Exception {
		return processRequest(jsonData, true);
	}

	private CRFVersionBean processRequest(String jsonData, boolean importCrfVersion) throws Exception {
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

	private CRFBean getCrfBean(String jsonData) throws Exception {
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

	private CRFVersionBean save(CrfBuilder crfBuilder, boolean importCrfVersion) throws Exception {
		ValidatorUtil.checkForErrors(crfBuilder.getErrorsList());
		CRFVersionBean crfVersionBean = crfBuilder.save();
		if (crfVersionBean.getId() == 0) {
			throw new RestException(messageSource,
					importCrfVersion ? "rest.importCrfVersion.operationFailed" : "rest.importCrf.operationFailed");
		}
		return crfVersionBean;
	}
}
