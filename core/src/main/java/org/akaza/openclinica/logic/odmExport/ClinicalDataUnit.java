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

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.dao.extract.OdmExtractDAO;

import java.util.HashMap;

import javax.sql.DataSource;

/**
 * A class for one ODM ClinicalData Element.
 * 
 * @author ywang (May, 2009)
 */

public class ClinicalDataUnit extends OdmUnit {
	private OdmClinicalDataBean odmClinicalData;
	private String studySubjectIds;
	private boolean skipBlanks;

	public ClinicalDataUnit() {
	}

	public ClinicalDataUnit(DataSource ds, StudyBean study, int category) {
		super(ds, study, category);
		this.odmClinicalData = new OdmClinicalDataBean();
	}

	public ClinicalDataUnit(DataSource ds, DatasetBean dataset, ODMBean odmBean, StudyBean study, int category) {
		super(ds, dataset, odmBean, study, category);
		this.odmClinicalData = new OdmClinicalDataBean();
	}

	public ClinicalDataUnit(DataSource ds, DatasetBean dataset, ODMBean odmBean, StudyBean study, int category,
			String studySubjectIds) {
		super(ds, dataset, odmBean, study, category);
		this.odmClinicalData = new OdmClinicalDataBean();
		this.studySubjectIds = studySubjectIds;
	}

	public void collectOdmClinicalData() {
		StudyBean study = studyBase.getStudy();
		String studyOID = study.getOid();
		if (studyOID == null || studyOID.length() <= 0) {
			logger.info("Constructed studyOID using study_id because oc_oid is missing from the table - study.");
			studyOID = "" + study.getId();
		}
		odmClinicalData.setStudyOID(studyOID);

		OdmExtractDAO oedao = new OdmExtractDAO(this.ds);
		if (this.getCategory() == 1 && study.isSite(study.getParentStudyId())) {
			String mvoid = "";
			if (this.dataset != null) {
				mvoid = this.dataset.getOdmMetaDataVersionOid();
			}
			if (mvoid.length() > 0) {
				mvoid += "-" + studyOID;
			} else {
				mvoid = "v1.0.0" + "-" + studyOID;
			}
			odmClinicalData.setMetaDataVersionOID(mvoid);

		} else {
			odmClinicalData.setMetaDataVersionOID(this.dataset.getOdmMetaDataVersionOid());
			if (odmClinicalData.getMetaDataVersionOID() == null
					|| odmClinicalData.getMetaDataVersionOID().length() <= 0) {
				odmClinicalData.setMetaDataVersionOID("v1.0.0");
			}
		}
		oedao.getClinicalData(study, this.dataset, odmClinicalData, this.odmBean.getODMVersion(), studySubjectIds,
				this.odmBean.getOdmType(), this.skipBlanks);
	}

	public OdmClinicalDataBean getOdmClinicalData() {
		return odmClinicalData;
	}

	public void setOdmClinicalData(OdmClinicalDataBean odmClinicalData) {
		this.odmClinicalData = odmClinicalData;
	}

	public static Boolean isNull(String itValue, String key, HashMap<String, String> nullValueCVs) {
		if (nullValueCVs.containsKey(key)) {
			String[] nullvalues = nullValueCVs.get(key).split(",");
			String[] values = itValue.split(",");
			for (String v : values) {
				v = v.trim();
				for (String n : nullvalues) {
					if (v.equals(n)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getStudySubjectIds() {
		return studySubjectIds;
	}

	public void setStudySubjectIds(String studySubjectIds) {
		this.studySubjectIds = studySubjectIds;
	}

	public boolean getSkipBlanks() {
		return skipBlanks;
	}

	public void setSkipBlanks(boolean skipBlanks) {
		this.skipBlanks = skipBlanks;
	}
}
