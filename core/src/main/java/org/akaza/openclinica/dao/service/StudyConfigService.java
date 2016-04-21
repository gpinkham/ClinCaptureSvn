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
package org.akaza.openclinica.dao.service;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameter;
import org.akaza.openclinica.bean.service.StudyParameterConfig;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.enums.SystemConfigurationParameters;
import com.clinovo.util.ReflectionUtil;

/**
 * StudyConfigService class.
 */
@Service
@SuppressWarnings({"rawtypes"})
public class StudyConfigService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudyConfigService.class);

	@Autowired
	private DataSource ds;

	/**
	 * Default constructor.
	 */
	public StudyConfigService() {
	}

	/**
	 * This method is used to initialize Study Config Service.
	 *
	 * @param ds
	 *            The data source to set.
	 */
	public StudyConfigService(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * @return Returns the ds.
	 */
	public DataSource getDs() {
		return ds;
	}

	/**
	 * @param ds
	 *            The ds to set.
	 */
	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * This method construct an object which has all the study parameter values.
	 *
	 * @param study
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean setParametersForStudy(StudyBean study) {
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		ArrayList parameters = spvdao.findAllParameters();
		StudyParameterConfig spc = new StudyParameterConfig();

		for (Object parameter : parameters) {
			StudyParameter sp = (StudyParameter) parameter;
			String handle = sp.getHandle();
			StudyParameterValueBean spv = spvdao.findByHandleAndStudy(study.getId(), handle);

			setStudyParameterValues(spvdao, spc, handle, spv);
		}
		study.setStudyParameterConfig(spc);
		return study;
	}

	/**
	 * Populates study bean.
	 *
	 * @param studyBean
	 *            StudyBean
	 * @return StudyBean
	 */
	public StudyBean populateStudyBean(StudyBean studyBean) {
		StudyDAO studyDao = new StudyDAO(ds);
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(ds);
		ArrayList studyParameters = spvdao.findParamConfigByStudy(studyBean);
		studyBean.setStudyParameters(studyParameters);
		StudyConfigService scs = new StudyConfigService(ds);
		if (studyBean.getParentStudyId() <= 0) {
			studyBean = scs.setParametersForStudy(studyBean);
		} else {
			studyBean.setParentStudyName((studyDao.findByPK(studyBean.getParentStudyId())).getName());
			studyBean = scs.setParametersForSite(studyBean);
		}

		return studyBean;
	}

	/**
	 * This method is used to set all study parameters to site.
	 *
	 * @param site
	 *            The <code>StudyBean</code> for which all site parameters should be set.
	 * @return The <code>StudyBean</code> for which all site parameters had been set
	 */
	public StudyBean setParametersForSite(StudyBean site) {
		return setParametersForStudy(site);
	}

	/**
	 * Updates study configuration parameter.
	 * 
	 * @param parameterName
	 *            String
	 * @param spc
	 *            StudyParameterConfig
	 * @param spv
	 *            StudyParameterValueBean
	 * @param spvdao
	 *            StudyParameterValueDAO
	 */
	public void updateParameter(String parameterName, StudyParameterConfig spc, StudyParameterValueBean spv,
			StudyParameterValueDAO spvdao) {
		try {
			spv.setParameter(parameterName);
			Method method = ReflectionUtil.getMethod(spc.getClass(), parameterName);
			spv.setValue((String) method.invoke(spc));
			updateParameter(spvdao, spv);
		} catch (Exception ex) {
			LOGGER.error("Error has occurred.", ex);
		}
	}

	private void updateParameter(StudyParameterValueDAO spvdao, StudyParameterValueBean spv) {
		StudyParameterValueBean spv1 = spvdao.findByHandleAndStudy(spv.getStudyId(), spv.getParameter());
		if (spv1.getId() > 0) {
			spvdao.update(spv);
		} else {
			spvdao.create(spv);
		}
	}

	private void setStudyParameterValues(StudyParameterValueDAO spvdao, StudyParameterConfig spc, String handle,
			StudyParameterValueBean spv) {
		if (spv.getId() > 0) {
			ReflectionUtil.setParameter(handle, spv.getValue(), spc);
		} else if (spv.getId() == 0) {
			setSystemParameterValues(spvdao, spc, handle);
		}
		if (handle.equalsIgnoreCase(SystemConfigurationParameters.DEFAULT_BIOONTOLOGY_URL.getName())) {
			setSystemParameterValues(spvdao, spc, handle);
		} else if (handle.equalsIgnoreCase(SystemConfigurationParameters.MEDICAL_CODING_API_KEY.getName())) {
			setSystemParameterValues(spvdao, spc, handle);
		}
	}

	private void setSystemParameterValues(StudyParameterValueDAO spvdao, StudyParameterConfig spc, String handle) {
		com.clinovo.model.System systemProp = spvdao.findSystemPropertyByName(handle);
		String value;
		if (systemProp != null) {
			value = systemProp.getValue();
			if (SystemConfigurationParameters.isPresent(handle)) {
				ReflectionUtil.setParameter(handle, value, spc);
			}
		}
	}
}
