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
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 *
 * Created on Jul 7, 2005
 */
package org.akaza.openclinica.bean.extract;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.ItemBean;

public class SEDColumn {
	private StudyEventDefinitionBean studyEventDefinition;
	private CRFBean crf;
	private ItemBean item;

	public SEDColumn(StudyEventDefinitionBean sedb, CRFBean cb, ItemBean ib) {
		this.studyEventDefinition = sedb;
		this.crf = cb;
		this.item = ib;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}

		SEDColumn other = (SEDColumn) obj;
		return other.studyEventDefinition.getId() == studyEventDefinition.getId() && other.crf.getId() == crf.getId()
				&& other.item.getId() == item.getId();
	}

	@Override
	public int hashCode() {
		String s = new String(studyEventDefinition.getId() + "-" + crf.getId() + "-" + item.getId());
		return s.hashCode();
	}

	/**
	 * @return Returns the crf.
	 */
	public CRFBean getCrf() {
		return crf;
	}

	/**
	 * @return Returns the item.
	 */
	public ItemBean getItem() {
		return item;
	}

	/**
	 * @return Returns the studyEventDefinition.
	 */
	public StudyEventDefinitionBean getStudyEventDefinition() {
		return studyEventDefinition;
	}
}
