/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */

package com.clinovo.service.impl;

import java.util.ArrayList;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetDao;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.util.EventDefinitionCRFUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.DeleteCrfService;

/**
 * DeleteCrfServiceImpl.
 */
@Service
@SuppressWarnings("rawtypes")
public class DeleteCrfServiceImpl implements DeleteCrfService {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private RuleSetDao ruleSetDao;

	/**
	 * {@inheritDoc}
	 */
	public void deleteCrf(int crfId) {
		new CRFDAO(dataSource).deleteCrfById(crfId);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deleteCrfVersion(int crfVersionId) {
		CRFVersionDAO crfVersionDao = new CRFVersionDAO(dataSource);
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfVersionId);
		if (crfVersionBean.getId() > 0) {
			ArrayList items = crfVersionDao.findNotSharedItemsByVersion(crfVersionBean.getId());
			EventDefinitionCRFUtil.setDefaultCRFVersionInsteadOfDeleted(dataSource, crfVersionBean.getId());
			ruleSetDao.deleteRuleStudioMetadataByCRFVersionOID(crfVersionBean.getOid());
			crfVersionDao.deleteCrfVersion(crfVersionBean, items);
		}
	}

}
