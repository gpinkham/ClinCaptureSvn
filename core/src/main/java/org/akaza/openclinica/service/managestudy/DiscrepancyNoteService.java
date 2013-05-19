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

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;

import java.sql.Connection;

/**
 * I am not really the author but I wanted to commit this code for an example with Jenkins.
 * 
 * @author Tom
 * 
 */
public class DiscrepancyNoteService implements IDiscrepancyNoteService {

	private DataSource ds;
	private DiscrepancyNoteDAO discrepancyNoteDao;

	public DiscrepancyNoteService(DataSource ds) {
		this.ds = ds;
	}

    public void saveFieldNotes(String description, int entityId, String entityType, StudyBean sb, UserAccountBean ub,
                               boolean assignToUser) {
        saveFieldNotes(description, entityId, entityType, null, sb, ub, assignToUser);
    }

	public void saveFieldNotes(String description, int entityId, String entityType, Connection connection, StudyBean sb, UserAccountBean ub,
			boolean assignToUser) {
		// Create a new thread each time
		DiscrepancyNoteBean parent = createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub, null,
				assignToUser);
		createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub, parent.getId(), assignToUser);
	}

    public void saveFieldNotes(String description, int entityId, String entityType, StudyBean sb, UserAccountBean ub) {
        saveFieldNotes(description, entityId, entityType, null, sb, ub);
    }

    public void saveFieldNotes(String description, int entityId, String entityType, Connection connection, StudyBean sb, UserAccountBean ub) {
		// Create a new thread each time
		DiscrepancyNoteBean parent = createDiscrepancyNoteBean(description, entityId, entityType, connection, sb, ub, null, false);
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
		dnb.setColumn("value");
		dnb.setOwner(ub);
		if (assignToUser) {
			dnb.setAssignedUser(ub);
			dnb.setAssignedUserId(ub.getId());
		}
		if (parentId != null) {
			dnb.setParentDnId(parentId);
		}
		dnb = (DiscrepancyNoteBean) getDiscrepancyNoteDao().create(dnb, connection);
		getDiscrepancyNoteDao().createMapping(dnb, connection);
		return dnb;
	}

	private DiscrepancyNoteDAO getDiscrepancyNoteDao() {
		discrepancyNoteDao = this.discrepancyNoteDao != null ? discrepancyNoteDao : new DiscrepancyNoteDAO(ds);
		return discrepancyNoteDao;
	}

}
