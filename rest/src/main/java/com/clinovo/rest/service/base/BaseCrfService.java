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
package com.clinovo.rest.service.base;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.clinovo.rest.model.Response;
import com.clinovo.rest.util.ValidatorUtil;
import com.clinovo.service.CrfVersionService;
import com.clinovo.service.DeleteCrfService;

/**
 * BaseCrfService.
 */
@SuppressWarnings("unchecked")
public abstract class BaseCrfService extends BaseService {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DeleteCrfService deleteCrfService;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Autowired
	private CrfVersionService crfVersionService;

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
		CRFBean crfBean = (CRFBean) getCRFDAO().findByNameAndStudy(crfName, getCurrentStudy());
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

	protected CRFBean getCrfBean(int crfId) throws Exception {
		CRFBean crfBean = (CRFBean) getCRFDAO().findByPK(crfId);
		if (crfBean.getId() == 0) {
			throw new RestException(messageSource, "rest.crfservice.crfWithIdDoesNotExist", new Object[]{crfId},
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (crfBean.getStudyId() != getCurrentStudy().getId()) {
			throw new RestException(messageSource, "rest.crfservice.crfWithIdDoesNotBelongToCurrentScope",
					new Object[]{crfId}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		List<CRFVersionBean> crfVersionBeanList = getCRFVersionDAO().findAllByCRFId(crfId);
		if (crfVersionBeanList != null) {
			crfBean.setCrfVersions(crfVersionBeanList);
		}
		return crfBean;
	}

	protected CRFVersionBean getCrfVersionBean(int crfVersionId, boolean checkCrfAvailability) throws Exception {
		CRFVersionBean crfVersionBean = (CRFVersionBean) getCRFVersionDAO().findByPK(crfVersionId);
		if (crfVersionBean.getId() == 0) {
			throw new RestException(messageSource, "rest.crfservice.crfVersionWithIdDoesNotExist",
					new Object[]{crfVersionId}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		CRFBean crfBean = (CRFBean) getCRFDAO().findByPK(crfVersionBean.getCrfId());
		if (crfBean.getStudyId() != getCurrentStudy().getId()) {
			throw new RestException(messageSource, "rest.crfservice.crfVersionDoesNotBelongToCurrentScope",
					new Object[]{crfVersionId}, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} else if (checkCrfAvailability && !crfBean.getStatus().isAvailable()) {
			throw new RestException(messageSource, "rest.crfservice.cannotPerformOperationBecauseCRFIsNotAvailable");
		}
		return crfVersionBean;
	}

	protected List<CRFBean> getAllCrfs() throws Exception {
		List<CRFVersionBean> crfVersionBeanList = getAllCrfVersions();
		List<CRFBean> crfBeanList = getCRFDAO().findAllCRFs(getCurrentStudy());
		Map<Integer, List<CRFVersionBean>> crfToVersionsMap = new HashMap<Integer, List<CRFVersionBean>>();
		for (CRFVersionBean crfVersionBean : crfVersionBeanList) {
			List<CRFVersionBean> crfVersionList = crfToVersionsMap.get(crfVersionBean.getCrfId());
			if (crfVersionList == null) {
				crfVersionList = new ArrayList<CRFVersionBean>();
				crfToVersionsMap.put(crfVersionBean.getCrfId(), crfVersionList);
			}
			crfVersionList.add(crfVersionBean);
		}
		for (CRFBean crfBean : crfBeanList) {
			List<CRFVersionBean> crfVersionList = crfToVersionsMap.get(crfBean.getId());
			if (crfVersionList != null) {
				crfBean.setCrfVersions(crfVersionList);
			}
		}
		return crfBeanList;
	}

	protected List<CRFVersionBean> getAllCrfVersions() throws Exception {
		return getCRFVersionDAO().findAllCRFVersions(getCurrentStudy());
	}

	protected CRFBean removeCrfBean(int crfId) throws Exception {
		CRFBean crfBean = getCrfBean(crfId);
		if (crfBean.getStatus().isLocked()) {
			throw new RestException(messageSource, "rest.crfservice.youCannotRemoveLockedCRF");
		} else if (!crfBean.getStatus().isDeleted()) {
			crfVersionService.removeCrf(crfBean, getCurrentUser());
		}
		return crfBean;
	}

	protected CRFBean restoreCrfBean(int crfId) throws Exception {
		CRFBean crfBean = getCrfBean(crfId);
		if (!crfBean.getStatus().isDeleted()) {
			throw new RestException(messageSource, "rest.crfservice.crfIsNotInRemovedState");
		} else {
			crfVersionService.restoreCrf(crfBean, getCurrentUser());
		}
		return crfBean;
	}

	protected CRFVersionBean removeCrfVersionBean(int crfVersionId) throws Exception {
		CRFVersionBean crfVersionBean = getCrfVersionBean(crfVersionId, true);
		if (crfVersionBean.getStatus().isLocked()) {
			throw new RestException(messageSource, "rest.crfservice.youCannotRemoveLockedCRFVersion");
		} else if (!crfVersionBean.getStatus().isDeleted()) {
			crfVersionService.removeCrfVersion(crfVersionBean, getCurrentUser());
		}
		return crfVersionBean;
	}

	protected CRFVersionBean restoreCrfVersionBean(int crfVersionId) throws Exception {
		CRFVersionBean crfVersionBean = getCrfVersionBean(crfVersionId, true);
		if (!crfVersionBean.getStatus().isDeleted()) {
			throw new RestException(messageSource, "rest.crfservice.crfVersionIsNotInRemovedState");
		} else {
			crfVersionService.restoreCrfVersion(crfVersionBean, getCurrentUser());
		}
		return crfVersionBean;
	}

	protected CRFVersionBean lockCrfVersionBean(int crfVersionId) throws Exception {
		CRFVersionBean crfVersionBean = getCrfVersionBean(crfVersionId, true);
		if (crfVersionBean.getStatus().isDeleted()) {
			throw new RestException(messageSource, "rest.crfservice.youCannotLockRemovedCRFVersion");
		} else if (!crfVersionBean.getStatus().isLocked()) {
			crfVersionService.lockCrfVersion(crfVersionBean, getCurrentUser());
		}
		return crfVersionBean;
	}

	protected CRFVersionBean unlockCrfVersionBean(int crfVersionId) throws Exception {
		CRFVersionBean crfVersionBean = getCrfVersionBean(crfVersionId, true);
		if (!crfVersionBean.getStatus().isLocked()) {
			throw new RestException(messageSource, "rest.crfservice.crfVersionIsNotInLockedState");
		} else {
			crfVersionService.unlockCrfVersion(crfVersionBean, getCurrentUser());
		}
		return crfVersionBean;
	}

	protected Response deleteCrfBean(int crfId, boolean force) throws Exception {
		deleteCrfService.deleteCrf(getCrfBean(crfId), getCurrentUser(), LocaleResolver.getLocale(), force);
		return new Response(String.valueOf(HttpServletResponse.SC_OK));
	}

	protected Response deleteCrfVersionBean(int crfVersionId, boolean force) throws Exception {
		deleteCrfService.deleteCrfVersion(getCrfVersionBean(crfVersionId, force), LocaleResolver.getLocale(), force);
		return new Response(String.valueOf(HttpServletResponse.SC_OK));
	}
}
