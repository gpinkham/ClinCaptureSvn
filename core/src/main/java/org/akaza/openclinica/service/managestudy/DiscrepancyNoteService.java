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
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * OpenClinica is distributed under the
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.service.managestudy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.admin.AuditBean;
import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.dao.admin.AuditDAO;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.apache.commons.lang.StringUtils;

/**
 * I am not really the author but I wanted to commit this code for an example with Jenkins.
 * 
 * @author Tom
 * 
 */
public class DiscrepancyNoteService implements IDiscrepancyNoteService {

	private DataSource ds;

	public static final String DN_ITEM_DATA_ENTITY_TYPE = "itemData";

	public static final String DN_COLUMN_VALUE = "value";

	/**
	 * 
	 * @param ds
	 *            DataSource to set
	 */
	public DiscrepancyNoteService(DataSource ds) {
		this.ds = ds;
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveFieldNotes(String description, int entityId, String entityType, StudyBean sb, UserAccountBean ub,
			boolean assignToUser) {
		saveFieldNotes(description, entityId, entityType, null, sb, ub, assignToUser);
	}

	/**
	 * Saves field DNs.
	 * 
	 * @param description
	 *            DN Description
	 * @param entityId
	 *            ItemData id
	 * @param entityType
	 *            Type of entity
	 * @param connection
	 *            DbConnection
	 * @param sb
	 *            Current study
	 * @param ub
	 *            Current user
	 * @param assignToUser
	 *            User to assign DN to
	 */
	public void saveFieldNotes(String description, int entityId, String entityType, Connection connection,
			StudyBean sb, UserAccountBean ub, boolean assignToUser) {
		// Create a new thread each time
		DiscrepancyNoteBean parent = createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub,
				null, assignToUser);
		createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub, parent.getId(), assignToUser);
	}

	/**
	 * {@inheritDoc}
	 */
	public void saveFieldNotes(String description, int entityId, String entityType, StudyBean sb, UserAccountBean ub) {
		saveFieldNotes(description, entityId, entityType, null, sb, ub);
	}

	/**
	 * Saves fields DNs.
	 * 
	 * @param description
	 *            DN Description
	 * @param entityId
	 *            ItemData id
	 * @param entityType
	 *            Type of entity
	 * @param connection
	 *            DbConnection
	 * @param sb
	 *            Current study
	 * @param ub
	 *            Current user
	 */
	public void saveFieldNotes(String description, int entityId, String entityType, Connection connection,
			StudyBean sb, UserAccountBean ub) {
		// Create a new thread each time
		DiscrepancyNoteBean parent = createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub,
				null, false);
		createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub, parent.getId(), false);
	}

	private DiscrepancyNoteBean createDiscrepancyNoteBean(String description, int entityId, String entityType,
			Connection connection, StudyBean sb, UserAccountBean ub, Integer parentId, boolean assignToUser) {
		DiscrepancyNoteBean dnb = new DiscrepancyNoteBean();
		dnb.setEntityId(entityId);
		dnb.setStudyId(sb.getId());
		dnb.setEntityType(entityType);
		dnb.setDescription(description);
		dnb.setDiscrepancyNoteTypeId(1);
		dnb.setResolutionStatusId(1);
		dnb.setColumn(DN_COLUMN_VALUE);
		dnb.setOwner(ub);
		if (assignToUser) {
			dnb.setAssignedUser(ub);
			dnb.setAssignedUserId(ub.getId());
		}
		if (parentId != null) {
			dnb.setParentDnId(parentId);
		}
		DiscrepancyNoteDAO discrepancyNoteDao = new DiscrepancyNoteDAO(ds);
		dnb = (DiscrepancyNoteBean) discrepancyNoteDao.create(dnb, connection);
		discrepancyNoteDao.createMapping(dnb, connection);
		return dnb;
	}

	/**
	 * Generates RFCs for fields changed e.g. during evaluation.
	 * 
	 * @param changedItems
	 *            List of changed fields
	 * @param changedItemNamesList
	 *            List of changed filed names
	 * @param oldItemData
	 *            Map containing old data
	 * @param ub
	 *            Current user
	 * @param noteDescription
	 *            DN description
	 * @param detailedDescription
	 *            DN detailed description
	 * 
	 * @return List of RFCs
	 */
	public List<DiscrepancyNoteBean> generateRFCsForChangedFields(List<DisplayItemBean> changedItems,
			List<String> changedItemNamesList, Map<Integer, String> oldItemData, UserAccountBean ub,
			String noteDescription, String detailedDescription) {
		List<DiscrepancyNoteBean> dbns = new ArrayList<DiscrepancyNoteBean>();
		int index = 0;
		for (DisplayItemBean changedItem : changedItems) {
			if (itemEligibleForAutoRFC(changedItem, oldItemData, ub)) {
				DiscrepancyNoteBean rfc = createRFC(changedItem, changedItemNamesList.get(index), ub,
						noteDescription, detailedDescription);
				dbns.add(rfc);
			}
			index++;
		}
		return dbns;
	}

	private DiscrepancyNoteBean createRFC(DisplayItemBean item, String field, UserAccountBean ub,
			String noteDescription, String detailedDescription) {
		DiscrepancyNoteBean note = new DiscrepancyNoteBean();
		note.setDescription(noteDescription);
		note.setDetailedNotes(detailedDescription);
		note.setOwner(ub);
		note.setCreatedDate(new Date());
		note.setResStatus(ResolutionStatus.NOT_APPLICABLE);
		note.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
		note.setDisType(DiscrepancyNoteType.REASON_FOR_CHANGE);
		note.setDiscrepancyNoteTypeId(DiscrepancyNoteType.REASON_FOR_CHANGE.getId());
		note.setItemId(item.getItem().getId());
		note.setEntityType(DN_ITEM_DATA_ENTITY_TYPE);
		note.setAssignedUser(ub);
		note.setAssignedUserId(ub.getId());
		note.setActive(true);
		note.setField(field);
		note.setColumn(DN_COLUMN_VALUE);
		return note;
	}

	private boolean itemEligibleForAutoRFC(DisplayItemBean changedItem, Map<Integer, String> oldItemData,
			UserAccountBean ub) {
		if (isNewItem(changedItem, oldItemData)) {
			return false;
		}
		if (itemHasRFC(changedItem)) {
			return false;
		} else if (userHasChangedItemValueFromBlankBefore(changedItem.getData(), ub)) {
			return false;
		}
		return true;
	}

	private boolean isNewItem(DisplayItemBean changedItem, Map<Integer, String> oldItemData) {
		if (changedItem.getData().getId() == 0) {
			return true;
		}
		String oldValue = oldItemData.get(changedItem.getData().getId());
		if (oldValue != null && StringUtils.isBlank(oldValue)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean itemHasRFC(DisplayItemBean item) {
		List<DiscrepancyNoteBean> existingNotes = (List<DiscrepancyNoteBean>) new DiscrepancyNoteDAO(ds)
				.findExistingNotesForItemData(item.getData().getId());
		if (existingNotes == null || existingNotes.isEmpty()) {
			return false;
		}
		for (DiscrepancyNoteBean note : existingNotes) {
			if (note.getDiscrepancyNoteTypeId() == DiscrepancyNoteType.REASON_FOR_CHANGE.getId()) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private boolean userHasChangedItemValueFromBlankBefore(ItemDataBean itemData, UserAccountBean ub) {
		List<AuditBean> itemDataAudits = new AuditDAO(ds).findItemAuditEvents(itemData.getId(), "item_data");
		for (AuditBean idAudit : itemDataAudits) {
			if (idAudit.getUserId() == ub.getId() && StringUtils.isBlank(idAudit.getOldValue())) {
				return true;
			}
		}
		return false;
	}
}