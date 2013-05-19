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
 * copyright 2003-2010 Akaza Research
 */
package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.domain.crfdata.SCDItemMetadataBean;

import java.util.ArrayList;
import java.util.List;

public class SCDItemMetadataDao extends AbstractDomainDao<SCDItemMetadataBean> {

	@Override
	Class<SCDItemMetadataBean> domainClass() {
		return SCDItemMetadataBean.class;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<SCDItemMetadataBean> findAllBySectionId(Integer sectionId) {
		String query = "select scd.* from scd_item_metadata scd where scd.scd_item_form_metadata_id in ("
				+ "select ifm.item_form_metadata_id from item_form_metadata ifm where ifm.section_id = :sectionId)";
		org.hibernate.Query q = this.getCurrentSession().createSQLQuery(query).addEntity(this.domainClass());
		q.setInteger("sectionId", sectionId);
		return (ArrayList<SCDItemMetadataBean>) q.list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> findAllSCDItemFormMetadataIdsBySectionId(Integer sectionId) {
		String query = "select scd.scd_item_form_metadata_id from scd_item_metadata scd where scd.scd_item_form_metadata_id in ("
				+ "select ifm.item_form_metadata_id from item_form_metadata ifm where ifm.section_id = :sectionId)";
		org.hibernate.Query q = this.getCurrentSession().createSQLQuery(query);
		q.setInteger("sectionId", sectionId);
		return q.list();
	}
}
