/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
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
 * OpenClinica is distributed under the GNU Lesser General Public License (GNU
 * LGPL).
 *
 * For details see: http://www.openclinica.org/license copyright 2003-2005 Akaza
 * Research
 *
 */

package org.akaza.openclinica.logic.odmExport;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.sql.DataSource;

/**
 * Gather information about an odm study.
 * 
 * @author ywang
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class OdmStudyBase {
	private StudyBean study;
	private List<StudyEventDefinitionBean> sedBeansInStudy;

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public OdmStudyBase() {
	}

	/**
	 * In this constructor, study, sedBeansInStudy has been initialized.
	 * 
	 * @param ds
	 * @param study
	 */
	public OdmStudyBase(DataSource ds, StudyBean study) {
		if (study == null) {
			logger.info("Study is null!");
			return;
		}
		this.study = study;
		int parentStudyId = this.study.getParentStudyId() > 0 ? this.study.getParentStudyId() : this.study.getId();
		this.sedBeansInStudy = new StudyEventDefinitionDAO(ds).findAllActiveByParentStudyId(parentStudyId);
	}

	public OdmStudyBase setOdmStudyBean(DataSource ds, StudyBean study) {
		OdmStudyBase studyBase = new OdmStudyBase();
		if (study == null) {
			logger.info("Study is null!");
		} else {
			this.study = study;
			int parentStudyId = this.study.getParentStudyId() > 0 ? this.study.getParentStudyId() : this.study.getId();
			this.sedBeansInStudy = new StudyEventDefinitionDAO(ds).findAllActiveByParentStudyId(parentStudyId);
		}
		return studyBase;
	}

	public void setStudy(StudyBean study) {
		this.study = study;
	}

	public StudyBean getStudy() {
		return this.study;
	}

	public void setSedBeansInStudy(List<StudyEventDefinitionBean> seds) {
		this.sedBeansInStudy = seds;
	}

	public List<StudyEventDefinitionBean> getSedBeansInStudy() {
		return this.sedBeansInStudy;
	}
}
