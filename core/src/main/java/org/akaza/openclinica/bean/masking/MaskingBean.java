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
 * Created on Sep 1, 2005
 *
 *
 */
package org.akaza.openclinica.bean.masking;

import org.akaza.openclinica.bean.core.AuditableEntityBean;

import java.util.HashMap;

/**
 * @author thickerson
 * 
 * 
 */
public class MaskingBean extends AuditableEntityBean {
	public HashMap ruleMap;// String property_name -> Boolean value
	public String entityName;
	public int entityId;
	public int studyId;
	public int roleId;

	public MaskingBean() {
		ruleMap = new HashMap();
	}

	public HashMap getRuleMap() {
		return this.ruleMap;
	}

	public void setRuleMap(HashMap ruleMap) {
		this.ruleMap = ruleMap;
	}

	/**
	 * @return Returns the entityId.
	 */
	public int getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId
	 *            The entityId to set.
	 */
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return Returns the entityName.
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName
	 *            The entityName to set.
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return Returns the roleId.
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId
	 *            The roleId to set.
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return Returns the studyId.
	 */
	public int getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            The studyId to set.
	 */
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
}
