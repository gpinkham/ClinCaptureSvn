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

package org.akaza.openclinica.dao.hibernate;

import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemGroupMetadataBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.domain.crfdata.DynamicsItemGroupMetadataBean;

public class DynamicsItemGroupMetadataDao extends AbstractDomainDao<DynamicsItemGroupMetadataBean> {

	@Override
	public Class<DynamicsItemGroupMetadataBean> domainClass() {
		return DynamicsItemGroupMetadataBean.class;
	}

	public DynamicsItemGroupMetadataBean findByMetadataBean(ItemGroupMetadataBean metadataBean,
			EventCRFBean eventCrfBean) {
		String query = "from "
				+ getDomainClassName()
				+ " metadata where metadata.itemGroupMetadataId = :id and metadata.itemGroupId = :item_group_id and metadata.eventCrfId = :event_crf_id ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("id", new Integer(metadataBean.getId()));
		q.setInteger("item_group_id", new Integer(metadataBean.getItemGroupId()));
		q.setInteger("event_crf_id", new Integer(eventCrfBean.getId()));
		return (DynamicsItemGroupMetadataBean) q.uniqueResult();
	}

	public DynamicsItemGroupMetadataBean findByMetadataBean(ItemGroupMetadataBean metadataBean, int eventCrfBeanId) {
		String query = "from "
				+ getDomainClassName()
				+ " metadata where metadata.itemGroupMetadataId = :id and metadata.itemGroupId = :item_group_id and metadata.eventCrfId = :event_crf_id ";
		org.hibernate.Query q = getCurrentSession().createQuery(query);
		q.setInteger("id", new Integer(metadataBean.getId()));
		q.setInteger("item_group_id", new Integer(metadataBean.getItemGroupId()));
		q.setInteger("event_crf_id", new Integer(eventCrfBeanId));
		return (DynamicsItemGroupMetadataBean) q.uniqueResult();
	}

	public Boolean hasShowingInSection(int sectionId, int crfVersionId, int eventCrfId) {
		String query = "";
		if ("oracle".equalsIgnoreCase(CoreResources.getDBName())) {
			query = "select dg.item_group_id from dyn_item_group_metadata dg where dg.event_crf_id = :eventCrfId and dg.item_group_metadata_id in ("
					+ " select distinct igm.item_group_metadata_id from item_group_metadata igm where igm.crf_version_id = :crfVersionId"
					+ " and igm.show_group = 0"
					+ " and igm.item_id in (select im.item_id from item_form_metadata im where im.section_id = :sectionId and im.crf_version_id = :crfVersionId))"
					+ " and dg.show_group = 1 and rownum = 1";
		} else {
			query = "select dg.item_group_id from dyn_item_group_metadata dg where dg.event_crf_id = :eventCrfId and dg.item_group_metadata_id in ("
					+ " select distinct igm.item_group_metadata_id from item_group_metadata igm where igm.crf_version_id = :crfVersionId"
					+ " and igm.show_group = 'false'"
					+ " and igm.item_id in (select im.item_id from item_form_metadata im where im.section_id = :sectionId and im.crf_version_id = :crfVersionId))"
					+ " and dg.show_group = 'true' limit 1";
		}

		org.hibernate.Query q = this.getCurrentSession().createSQLQuery(query);
		q.setInteger("eventCrfId", eventCrfId);
		q.setInteger("crfVersionId", crfVersionId);
		q.setInteger("sectionId", sectionId);
		q.setInteger("crfVersionId", crfVersionId);
		return q.list() != null && q.list().size() > 0;
	}
}
