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

import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.i18n.LocaleResolver;
import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;
import com.clinovo.rest.exception.RestException;
import com.clinovo.rest.util.ValidatorUtil;

/**
 * BaseCrfService.
 */
public abstract class BaseCrfService extends BaseService {

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Autowired
	private MessageSource messageSource;

	private CRFVersionBean save(CrfBuilder crfBuilder, boolean importCrfVersion) throws Exception {
		ValidatorUtil.checkForErrors(crfBuilder.getErrorsList());
		CRFVersionBean crfVersionBean = crfBuilder.save();
		if (crfVersionBean.getId() == 0) {
			throw new RestException(messageSource, importCrfVersion
					? "rest.crfservice.importcrfversion.operationFailed"
					: "rest.crfservice.importcrf.operationFailed");
		}
		return crfVersionBean;
	}

	private CRFBean getCrfBean(String jsonData) throws Exception {
		JSONObject jsonObject = new JSONObject(jsonData);
		String crfName = URLDecoder.decode(jsonObject.getString(NAME), UTF_8).trim();
		if (crfName.isEmpty()) {
			throw new RestException(messageSource, "rest.crfservice.importcrf.crfNameIsEmpty");
		}
		CRFBean crfBean = (CRFBean) getCRFDAO().findByName(crfName);
		if (crfBean.getId() == 0) {
			throw new RestException(messageSource, "rest.crfservice.importcrf.crfNameDoesNotExist",
					new Object[]{crfName}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return crfBean;
	}

	protected CRFVersionBean importCrf(String jsonData, boolean importCrfVersion) throws Exception {
		UserAccountBean owner = getCurrentUser();
		StudyBean currentStudy = getCurrentStudy();
		CrfBuilder crfBuilder = crfBuilderFactory.getCrfBuilder(jsonData, currentStudy, owner,
				LocaleResolver.getLocale(), messageSource);
		if (importCrfVersion) {
			crfBuilder.build(getCrfBean(jsonData).getId());
		} else {
			crfBuilder.build();
		}
		return save(crfBuilder, importCrfVersion);
	}
}
