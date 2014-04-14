/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.bean.admin;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.oid.CrfOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * <P>
 * CRFBean.java, the object for instruments in the database. Each one of these is linked to a version, and are
 * associated with studies through study events.
 * 
 * @author thickerson
 * 
 * 
 */
@SuppressWarnings({"rawtypes", "serial"})
public class CRFBean extends AuditableEntityBean {
	private int statusId = 1;
	private String description = "";
	private ArrayList versions;// not in DB
	private boolean selected = false; // not in DB

	private String oid;
	private OidGenerator oidGenerator;
	private int studyId;

	public CRFBean() {
		this.oidGenerator = new CrfOidGenerator();
		versions = new ArrayList();
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the statusId.
	 */
	public int getStatusId() {
		return statusId;
	}

	/**
	 * @param statusId
	 *            The statusId to set.
	 */
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return Returns the versions.
	 */
	public ArrayList getVersions() {
		return versions;
	}

	/**
	 * @param versions
	 *            The versions to set.
	 */
	public void setVersions(ArrayList versions) {
		this.versions = versions;
	}

	public int getVersionNumber() {
		return this.versions.size();
	}

	/**
	 * @return Returns the selected.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            The selected to set.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public OidGenerator getOidGenerator(DataSource ds) {
		if (oidGenerator != null) {
			oidGenerator.setDataSource(ds);
		}
		return oidGenerator;
	}

	public void setOidGenerator(OidGenerator oidGenerator) {
		this.oidGenerator = oidGenerator;
	}

	public int getStudyId() {
		return studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}
}
