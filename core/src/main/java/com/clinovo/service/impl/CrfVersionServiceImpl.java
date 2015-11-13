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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/

package com.clinovo.service.impl;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.CrfVersionService;
import com.clinovo.service.EventCRFService;
import com.clinovo.service.EventDefinitionCrfService;

/**
 * CrfVersionServiceImpl.
 */
@Service
@SuppressWarnings("unchecked")
public class CrfVersionServiceImpl implements CrfVersionService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private EventCRFService eventCRFService;

	@Autowired
	private EventDefinitionCrfService eventDefinitionCrfService;

	private CRFDAO getCrfDao() {
		return new CRFDAO(dataSource);
	}

	private CRFVersionDAO getCrfVersionDao() {
		return new CRFVersionDAO(dataSource);
	}

	private void disableVersion(CRFVersionBean crfVersionBean, UserAccountBean updater, Status status)
			throws Exception {
		crfVersionBean.setStatus(status);
		crfVersionBean.setUpdater(updater);
		crfVersionBean.setUpdatedDate(new Date());
		getCrfVersionDao().update(crfVersionBean);
	}

	private void enableVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception {
		crfVersionBean.setUpdater(updater);
		crfVersionBean.setUpdatedDate(new Date());
		crfVersionBean.setStatus(Status.AVAILABLE);
		getCrfVersionDao().update(crfVersionBean);
	}

	private void disableCrfVersions(CRFBean crfBean, UserAccountBean updater, Status status) throws Exception {
		List<CRFVersionBean> crfVersionBeanList = (List<CRFVersionBean>) getCrfVersionDao()
				.findAllByCRFId(crfBean.getId());
		for (CRFVersionBean crfVersionBean : crfVersionBeanList) {
			if (crfVersionBean.getStatus().isAvailable()) {
				disableVersion(crfVersionBean, updater, status);
			}
		}
	}

	private void enableCrfVersions(CRFBean crfBean, UserAccountBean updater) throws Exception {
		List<CRFVersionBean> crfVersionBeanList = (List<CRFVersionBean>) getCrfVersionDao()
				.findAllByCRFId(crfBean.getId());
		for (CRFVersionBean crfVersionBean : crfVersionBeanList) {
			if (crfVersionBean.getStatus().isAutoDeleted()) {
				enableVersion(crfVersionBean, updater);
			}
		}
	}

	private void disableCrf(CRFBean crfBean, UserAccountBean updater, Status status) throws Exception {
		crfBean.setStatus(status);
		crfBean.setUpdater(updater);
		crfBean.setUpdatedDate(new Date());
		getCrfDao().update(crfBean);
		disableCrfVersions(crfBean, updater, Status.AUTO_DELETED);
		eventDefinitionCrfService.removeParentEventDefinitionCRFs(crfBean, updater);
	}

	private void enableCrf(CRFBean crfBean, UserAccountBean updater) throws Exception {
		crfBean.setUpdater(updater);
		crfBean.setUpdatedDate(new Date());
		crfBean.setStatus(Status.AVAILABLE);
		getCrfDao().update(crfBean);
		enableCrfVersions(crfBean, updater);
		eventDefinitionCrfService.restoreParentEventDefinitionCRFs(crfBean, updater);
	}

	private void disableCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater, Status status)
			throws Exception {
		disableVersion(crfVersionBean, updater, status);
		eventDefinitionCrfService.updateDefaultVersionOfEventDefinitionCRF(crfVersionBean, updater);
		if (status.isDeleted()) {
			eventCRFService.removeEventCRFs(crfVersionBean, updater);
		} else {
			eventCRFService.lockEventCRFs(crfVersionBean, updater);
		}
	}

	private void enableCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater, boolean restore)
			throws Exception {
		enableVersion(crfVersionBean, updater);
		if (restore) {
			eventCRFService.restoreEventCRFs(crfVersionBean, updater);
		} else {
			eventCRFService.unlockEventCRFs(crfVersionBean, updater);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeCrf(CRFBean crfBean, UserAccountBean updater) throws Exception {
		disableCrf(crfBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreCrf(CRFBean crfBean, UserAccountBean updater) throws Exception {
		enableCrf(crfBean, updater);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception {
		disableCrfVersion(crfVersionBean, updater, Status.DELETED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void restoreCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception {
		enableCrfVersion(crfVersionBean, updater, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void lockCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception {
		disableCrfVersion(crfVersionBean, updater, Status.LOCKED);
	}

	/**
	 * {@inheritDoc}
	 */
	public void unlockCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) throws Exception {
		enableCrfVersion(crfVersionBean, updater, false);
	}
}
