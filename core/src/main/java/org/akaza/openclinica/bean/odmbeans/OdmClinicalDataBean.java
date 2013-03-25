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

package org.akaza.openclinica.bean.odmbeans;

import org.akaza.openclinica.bean.submit.crfdata.ExportSubjectDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ywang (May, 2008)
 * 
 */

public class OdmClinicalDataBean {
	private String studyOID;
	private String metaDataVersionOID;
	private List<ExportSubjectDataBean> exportSubjectData;

	/*
	 * private List<StudyEventDataBean> studyEventData; private List<FormDataBean> formData; private
	 * List<ImportItemGroupDataBean> itemGroupData; private List<ImportItemDataBean> itemData;
	 */

	public OdmClinicalDataBean() {
		exportSubjectData = new ArrayList<ExportSubjectDataBean>();
		/*
		 * studyEventData = new ArrayList<StudyEventDataBean>(); formData = new ArrayList<FormDataBean>(); itemGroupData
		 * = new ArrayList<ImportItemGroupDataBean>(); itemData = new ArrayList<ImportItemDataBean>();
		 */
	}

	public void setStudyOID(String studyOID) {
		this.studyOID = studyOID;
	}

	public String getStudyOID() {
		return this.studyOID;
	}

	public void setMetaDataVersionOID(String metaDataVersionOID) {
		this.metaDataVersionOID = metaDataVersionOID;
	}

	public String getMetaDataVersionOID() {
		return this.metaDataVersionOID;
	}

	public void setExportSubjectData(List<ExportSubjectDataBean> subject) {
		this.exportSubjectData = subject;
	}

	public List<ExportSubjectDataBean> getExportSubjectData() {
		return this.exportSubjectData;
	}

	/*
	 * public void setStudyEventData(List<StudyEventDataBean> studyEventData) { this.studyEventData = studyEventData; }
	 * 
	 * public List<StudyEventDataBean> getStudyEventData() { return this.studyEventData; }
	 * 
	 * public void setFormData(List<FormDataBean> formData) { this.formData = formData; }
	 * 
	 * public List<FormDataBean> getFormData() { return this.formData; }
	 * 
	 * public void setItemGroupData(List<ImportItemGroupDataBean> itemGroupData) { this.itemGroupData = itemGroupData; }
	 * 
	 * public List<ImportItemGroupDataBean> getItemGroupData() { return this.itemGroupData; }
	 * 
	 * public void setItemData(List<ImportItemDataBean> itemData) { this.itemData = itemData; }
	 * 
	 * public List<ImportItemDataBean> getItemData() { return this.itemData; }
	 */
}
