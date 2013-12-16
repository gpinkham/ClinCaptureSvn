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

import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.OdmClinicalDataBean;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;

/**
 * Populate ODM ClinicalData Element for a ODM XML file. It supports:
 * <ul>
 * <li>ODM XML file contains only one ODM ClinicalData element.</li>
 * <li>ODM XML file contains multiple ClinicalData elements - one parent study and its site(s).</li>
 * </ul>
 * 
 * @author ywang (May, 2009)
 */

public class ClinicalDataCollector extends OdmDataCollector {
	private LinkedHashMap<String, OdmClinicalDataBean> odmClinicalDataMap;

	/**
	 * 
	 * @param ds
	 * @param dataset
	 */
	public ClinicalDataCollector(DataSource ds, DatasetBean dataset, StudyBean currentStudy) {
		super(ds, dataset, currentStudy);
		this.odmClinicalDataMap = new LinkedHashMap<String, OdmClinicalDataBean>();
	}

	@Override
	public void collectFileData() {
		this.collectOdmRoot();
		this.collectOdmClinicalDataMap();
	}

	public void collectOdmClinicalDataMap() {
		Iterator<OdmStudyBase> it = this.getStudyBaseMap().values().iterator();
		while (it.hasNext()) {
			OdmStudyBase u = it.next();
			ClinicalDataUnit cdata = new ClinicalDataUnit(this.ds, this.dataset, this.getOdmbean(), u.getStudy(),
					this.getCategory());
			cdata.setCategory(this.getCategory());
			StudySubjectDAO ssdao = new StudySubjectDAO(this.ds);
			cdata.setStudySubjectIds(ssdao.findStudySubjectIdsByStudyIds(u.getStudy().getId() + ""));
			cdata.collectOdmClinicalData();
			odmClinicalDataMap.put(u.getStudy().getOid(), cdata.getOdmClinicalData());
		}
	}

	public LinkedHashMap<String, OdmClinicalDataBean> getOdmClinicalDataMap() {
		return odmClinicalDataMap;
	}

	public void setOdmClinicalDataMap(LinkedHashMap<String, OdmClinicalDataBean> odmClinicalDataMap) {
		this.odmClinicalDataMap = odmClinicalDataMap;
	}
}
