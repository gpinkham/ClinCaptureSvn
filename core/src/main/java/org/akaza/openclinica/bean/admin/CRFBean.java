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

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.akaza.openclinica.bean.core.AuditableEntityBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.oid.CrfOidGenerator;
import org.akaza.openclinica.bean.oid.OidGenerator;
import org.akaza.openclinica.bean.submit.CRFVersionBean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <P>
 * CRFBean.java, the object for instruments in the database. Each one of these is linked to a version, and are
 * associated with studies through study events.
 * 
 * @author thickerson
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Crf", namespace = "http://www.cdisc.org/ns/odm/v1.3")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@SuppressWarnings({"rawtypes", "serial"})
public class CRFBean extends AuditableEntityBean {

	@JsonProperty("oid")
	@XmlElement(name = "Oid", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String oid;

	@JsonProperty("crfName")
	@XmlElement(name = "CrfName", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private String name = "";

	@JsonProperty("crfVersions")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@XmlElement(name = "CrfVersion", namespace = "http://www.cdisc.org/ns/odm/v1.3")
	private List<CRFVersionBean> crfVersions = new ArrayList<CRFVersionBean>();

	private int statusId = 1;
	private String description = "";
	private String source = "";
	private ArrayList versions; // not in DB
	private boolean selected = false; // not in DB

	private OidGenerator oidGenerator;
	private int studyId;
	private ArrayList<StudyBean> studiesWhereUsed;

	/**
	 * Determines if a given CRF should use auto-layout or not. True indicates that auto layout should be applied, False
	 * otherwise
	 */
	private boolean autoLayout;

	/**
	 * CRFBean constructor.
	 */
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

	@Override
	public void setName(String name) {
		super.name = name;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Returns OidGenerator.
	 * 
	 * @param ds
	 *            DataSource
	 * @return OidGenerator
	 */
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

	/**
	 * @return Returns the autoLayout.
	 */
	public boolean isAutoLayout() {
		return autoLayout;
	}

	/**
	 * @param autoLayout
	 *            The autoLayout to set.
	 */
	public void setAutoLayout(boolean autoLayout) {
		this.autoLayout = autoLayout;
	}

	/**
	 * @return Source of the CRF - excel or form studio
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            - excel or form studio
	 */
	public void setSource(String source) {
		this.source = source;
	}

	public ArrayList<StudyBean> getStudiesWhereUsed() {
		return studiesWhereUsed == null ? new ArrayList<StudyBean>() : studiesWhereUsed;
	}

	public void setStudiesWhereUsed(ArrayList<StudyBean> studiesWhereUsed) {
		this.studiesWhereUsed = studiesWhereUsed;
	}

	public List<CRFVersionBean> getCrfVersions() {
		return crfVersions;
	}

	public void setCrfVersions(List<CRFVersionBean> crfVersions) {
		this.crfVersions = crfVersions;
	}
}
