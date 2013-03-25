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
 * If not, see <http://www.gnu.org/licenses/>.
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
import org.akaza.openclinica.bean.odmbeans.OdmAdminDataBean;

/**
 * Populate ODM AdminData Element for a ODM XML file. It supports:
 * <ul>
 * <li>ODM XML file contains only one ODM AdminData element.</li>
 * <li>ODM XML file contains multiple AdminData elements - one parent study and its site(s).</li>
 * </ul>
 * 
 * @author ywang (March, 2010)
 */

public class AdminDataCollector extends OdmDataCollector {
	private LinkedHashMap<String, OdmAdminDataBean> odmAdminDataMap;

	public AdminDataCollector(DataSource ds, StudyBean currentStudy) {
		super(ds, currentStudy);
		this.odmAdminDataMap = new LinkedHashMap<String, OdmAdminDataBean>();
	}

	/**
	 * 
	 * @param ds
	 * @param dataset
	 */
	public AdminDataCollector(DataSource ds, DatasetBean dataset, StudyBean currentStudy) {
		super(ds, dataset, currentStudy);
		this.odmAdminDataMap = new LinkedHashMap<String, OdmAdminDataBean>();
	}

	@Override
	public void collectFileData() {
		this.collectOdmAdminDataMap();
	}

	public void collectOdmAdminDataMap() {
		Iterator<OdmStudyBase> it = this.getStudyBaseMap().values().iterator();
		while (it.hasNext()) {
			OdmStudyBase u = it.next();
			AdminDataUnit adata = new AdminDataUnit(this.ds, this.dataset, this.getOdmbean(), u.getStudy(),
					this.getCategory());
			adata.setCategory(this.getCategory());
			adata.collectOdmAdminData();
			odmAdminDataMap.put(u.getStudy().getOid(), adata.getOdmAdminData());
		}
	}

	public LinkedHashMap<String, OdmAdminDataBean> getOdmAdminDataMap() {
		return odmAdminDataMap;
	}

	public void setOdmClinicalDataMap(LinkedHashMap<String, OdmAdminDataBean> odmAdminDataMap) {
		this.odmAdminDataMap = odmAdminDataMap;
	}
}
